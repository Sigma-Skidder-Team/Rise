# Rise

> [!WARNING]
> As of 12/29/2025 CDT, you can't authenticate to Rise's auth server without a valid HWID and username.
> The (99% sure it's the last, probably unless auth turns off again) version of this repository is going to be 0.0.5.
>
> 0.0.5 updates S2CPacketAuthenticationFinish, so it properly parses auth finish packets that aren't successful:
> - moved `pi`, `maxPitch`, and `serverTimeMS` into a data class named `AuthenticatedOnlyData`
> - replaced all of those with a single, nullable field named `aod` (of type `AuthenticatedOnlyData?`)
> 
> This is a breaking change for anything dependent on those fields
> (probably no one uses them, they are just there for completeness)
> 
> RIP Auth-less Rise, ~10/12/2025 to 12/29/2025
> This lasted a REALLY long time... But hey, at least I had some fun out of it! (only spamming IRC, but it was funny)

lets you connect to Rise auth servers,
with proper credentials.

## Usage

See [Example.kt](src/test/kotlin/Example.kt) or [JavaExample.java](src/test/java/JavaExample.java).

### Gradle (Kotlin DSL)

- Add JitPack to your repos

```kotlin
maven { url = uri("https://jitpack.io") }
```
- Add the dependency
```kotlin
// check for the latest version, this isn't always updated!
implementation("com.github.Sigma-Skidder-Team:Rise:0.0.5")
```

### Maven

- Add JitPack to your repos

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

- Add the dependency

```xml
<dependency>
    <groupId>com.github.Sigma-Skidder-Team</groupId>
    <artifactId>Rise</artifactId>
    <version>0.0.4</version>
</dependency>
```

## Waffled

![help fix pls](lol/help%20fix.png)
![I didn't know it was a missing header yet lol](lol/didn't%20even%20need%20to,%20it%20was%20a%20missing%20header.png)
![how to add header????](lol/how%20to%20add%20header.png)
