# Building from Source
## Warning
⚠️ Warning! ⚠️
Your should **ONLY** proceed, if you **REALLY** know, what you are doing.

---

## General
This project is being built, using [Maven](https://maven.apache.org/). <br>
This requires you to have maven installed in your PATH. You can check this running.
```bash
mvn --version
```
If this command does not throw you an error, Maven is installed.

---

## System Requirements
### Required
- Java version 13 or higher
- Maven version 3.6 or higher
- 2 GB of RAM
- One core CPU with clock speed higher than 2 GHz

### Recommended
- Java version 15 or higher
- Maven version 3.6.3 or higher
- 8 GM of RAM
- 4 or more CPU cores with a clock speed higher than 3 GHz
- [git](ttps://git-scm.com/downloads) version 2.31.1 or higher

---

## Building
### MacOS and Linux
1. Get the latest file from GitHub. If you have git installed (Check with `git --version`), use the following command in your command line (`cmd` on Windows, `Terminal` on MacOS and Linux):
```bash
git clone https://github.com/MaFeLP/MCDC.git
cd MCDC/
```

If git is not installed, go to [the project's GitHub Page](https://github.com/MaFeLP/MCDC), click on the green download Code button and download the code as ZIP. Then unzip the file and open a command line in this file.

2. On Linux and MacOS execute the script [bash-scripts/build.sh](https://github.com/MaFeLP/MCDC/blob/stable/bash-scripts/build.sh), by using the following command:
```bash
bash ./bash-scripts/build.sh
```

or, if you want to go the manual way or are on Windows, copy and paste the following commands into your shell:

```bash
mvn clean
mvn validate
mvn test
mvn package
mvn verify
```

3. In your file explorer, navigate to the `target` folder, inside the MCDC folder.
