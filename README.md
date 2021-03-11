<p align="center">
<img src="https://www.codefactor.io/repository/github/ai-null/oprek/badge" />
</p>

<h1 align="center">Oprek</h1>

<p align="center">
Oprek is a simple project management application made with Kotlin. <br /> The illustration used in this app can be found at <b><a href="https://opendoodles.com/">OpenDoodles</a></b>.
</p>

<p align="center">
<img src="https://raw.githubusercontent.com/ai-null/oprek/dev/demo/login.png" width="30%" />
<img src="https://raw.githubusercontent.com/ai-null/oprek/dev/demo/dashboard.png" width="30%" />
<img src="https://raw.githubusercontent.com/ai-null/oprek/dev/demo/detail_project.png" width="30%" />
</p>

___
## Architecture

* min Sdk 23
* [Kotlin](https://kotlinlang.org/), [Courotines](https://developer.android.com/kotlin/coroutines) for threading.
* Jetpack
  * LiveData - data handler
  * lifecycle - data changes observer
  * ViewModel - UI related data holder
  * Room - local Database
  * crypto - for Safely manage keys and encrypt files and sharedpreferences.
* MVVM Architecture (Model - View - ViewModel)

___
## Depedencies
| Library | Description |
| ------  | ----------- |
| [Glide](https://github.com/bumptech/glide) | lightweight image processing library |
| [Material Components](https://github.com/material-components/material-components-android) | UI styling library |
| [Room Database](https://developer.android.com/jetpack/androidx/releases/room) | local database |
| [Crypto](https://developer.android.com/reference/androidx/security/crypto/package-summary) | Used for sharedPreferences |

___
## License
```
MIT License

Copyright (c) 2020 Ainul

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
