# NumberToWords - Indonesian TERBILANG Library

[![JitPack](https://jitpack.io/v/yadev-source/NumberToWords.svg)](https://jitpack.io/#yadev-source/NumberToWords)
[![Release](https://img.shields.io/github/v/release/yadev-source/NumberToWords?label=release)](https://github.com/yadev-source/NumberToWords/releases)
[![License: 【entity-MIT¦canonical_name=MIT】](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Library Java ringan untuk convert angka jadi kata berbahasa Indonesia.

> `12345` -> `DUA BELAS RIBU TIGA RATUS EMPAT PULUH LIMA`

### Fitur
- ✅ Support SERIBU & SERATUS (bukan SATU RIBU)
- ✅ Support negatif MINUS, desimal KOMA
- ✅ Support sampai KUADRILIUN (10^15)
- ✅ 3 tipe input: long, String, double
- ✅ 1 file, 0 dependency, Java 8+

### Instalasi via JitPack

**settings.gradle:**
```gradle
repositories {
  google()
  mavenCentral()
  maven { url 'https://jitpack.io' }
}
```
**Gradle**
```dependency
implementation 'com.github.yadev-source:NumberToWords:v1.0.0'
```

```Cara pakai
import com.yadev.numbertowords.NumberToWords;

NumberToWords.convert(1001); // SERIBU SATU
NumberToWords.convert(100); // SERATUS
NumberToWords.convert(12345); // DUA BELAS RIBU TIGA RATUS EMPAT PULUH LIMA
NumberToWords.convert("1500000"); // SATU JUTA LIMA RATUS RIBU
NumberToWords.convert(-20); // MINUS DUA PULUH
NumberToWords.convert(123.45); // SERATUS DUA PULUH TIGA KOMA EMPAT LIMA
```

Lisensi
MIT License
