# 🛡️ Threats — Анализ уязвимостей WeatherApp

## 1. API-ключ захардкожен в исходном коде

**Файл:** `app/build.gradle.kts`, строка 22

```kotlin
buildConfigField("String", "WEATHER_API_KEY", "\"376be69cf22e4159bc5180644260203\"")
```

**В чём проблема:**  
API-ключ от WeatherAPI лежит прямо в файле `build.gradle.kts`, который коммитится в Git. Любой, кто получит доступ к репозиторию (или декомпилирует APK), сможет использовать этот ключ — например, для массовых запросов за ваш счёт или для исчерпания лимита бесплатного плана.

**Как исправить:**  
Хранить ключ в файле `local.properties` (он добавлен в `.gitignore`) и читать его в `build.gradle.kts` через `project.properties`:

```kotlin
val apiKey: String = project.findProperty("WEATHER_API_KEY") as? String ?: ""
buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
```

---

## 2. Разрешён Cleartext Traffic (HTTP без шифрования)

**Файл:** `AndroidManifest.xml`, строка 10

```xml
android:usesCleartextTraffic="true"
```

**В чём проблема:**  
Этот флаг разрешает приложению отправлять данные по незащищённому HTTP (без TLS/SSL). Это означает, что трафик между приложением и сервером может быть перехвачен атакующим в той же сети (атака Man-in-the-Middle). Он сможет увидеть API-ключ, данные о местоположении пользователя (город), и подменить ответ сервера.

**Как исправить:**  
Убрать `android:usesCleartextTraffic="true"` или поставить `false`. Все запросы к WeatherAPI и так идут по HTTPS — явного разрешения на HTTP не нужно.

---

## 3. Отсутствие валидации пользовательского ввода

**Файл:** `WeatherViewModel.kt`, строка 25 + `WeatherScreen.kt`, строка 118

```kotlin
fun searchWeather(city: String) {
    if (city.isBlank()) return
    // город напрямую передаётся в API-запрос...
}
```

**В чём проблема:**  
Пользовательский ввод (название города) передаётся в API-запрос без какой-либо фильтрации. Хотя Retrofit кодирует параметры, отсутствие ограничений означает:
- Можно отправить очень длинную строку — потенциальная нагрузка на сеть и API
- Можно вводить спецсимволы, которые могут повлиять на поведение API
- Нет ограничения на частоту запросов — пользователь может нажимать «Search» много раз подряд, быстро расходуя лимит API-ключа

**Как исправить:**  
Добавить базовую проверку: ограничить длину ввода, фильтровать допустимые символы, а также добавить debounce или cooldown на кнопку поиска.

---

## 4. Логирование HTTP-трафика в production-сборке

**Файл:** `NetworkModule.kt`, строки 22–23

```kotlin
val logging = HttpLoggingInterceptor()
logging.level = HttpLoggingInterceptor.Level.BODY
```

**В чём проблема:**  
`HttpLoggingInterceptor` с уровнем `BODY` логирует **всё** тело HTTP-запроса и ответа, включая API-ключ в query-параметрах. Этот interceptor подключён безусловно — и в debug, и в release-сборке. Если устройство скомпрометировано или логи доступны (например, через `adb logcat`), атакующий получит полный доступ к API-ключу и данным.

**Как исправить:**  
Включать логирование только в debug-сборке:

```kotlin
val logging = HttpLoggingInterceptor()
logging.level = if (BuildConfig.DEBUG) {
    HttpLoggingInterceptor.Level.BODY
} else {
    HttpLoggingInterceptor.Level.NONE
}
```

---

## 5. Отсутствие обфускации — лёгкий реверс-инжиниринг APK

**Файлы:** `app/build.gradle.kts` (строки 26–31), `proguard-rules.pro`

```kotlin
// build.gradle.kts
release {
    isMinifyEnabled = false   // ← обфускация ВЫКЛЮЧЕНА
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
```

```pro
# proguard-rules.pro — файл полностью пустой (только комментарии)
```

**В чём проблема:**  
Флаг `isMinifyEnabled = false` означает, что в release-сборке **не применяется R8/ProGuard**. Это значит:

1. **Весь код остаётся в читаемом виде** — имена классов, методов и переменных сохраняются как есть. Любой человек с `jadx` или `apktool` может декомпилировать APK и увидеть полную бизнес-логику, структуру API-запросов, ключи из `BuildConfig`, и все URL.
2. **Неиспользуемый код не удаляется** — код debug-инструментов (например, Chucker, интеграция с LeakCanary) может частично попасть в release-APK, расширяя поверхность атаки.
3. **Больший размер APK** — но это уже скорее побочный эффект.

В комбинации с уязвимостью #1 (захардкоженный ключ), это особенно опасно: атакующий декомпилирует APK за 30 секунд, видит `BuildConfig.WEATHER_API_KEY` в чистом виде и получает ключ.

**Как исправить:**  
Включить R8 и добавить правила для Retrofit/Gson:

```kotlin
// build.gradle.kts
release {
    isMinifyEnabled = true
    isShrinkResources = true
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
```

```pro
# proguard-rules.pro

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson — сохранить DTO-модели (они маппятся по именам полей)
-keep class com.example.weatherapp.data.remote.** { *; }

# Dagger
-keep class dagger.** { *; }
-keep class * extends dagger.internal.Factory
```

> Это менее очевидная уязвимость, потому что приложение работает «как обычно» и без обфускации. Проблема проявляется только когда APK попадает к злоумышленнику — а в случае публикации в Play Store это неизбежно.
