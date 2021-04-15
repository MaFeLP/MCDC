# Installation
## Quick Installation
1. Download the latest [release](https://github.com/MaFeLP/MCDC/releases/) and put it into `<your server directory>/plugins`.
2. Restart the server.
3. Get your discord bot token.
4. Go into the `<serverDirectory>/plugins/MCDC/config.yml` file and change the value of `apiToken` to your token.
4. OR Go into the console of your server and type `token <your discord bot token>` <br>

## Detailed installation
1. Go to this page [https://github.com/MaFeLP/MCDC/releases/latest](https://github.com/MaFeLP/MCDC/releases/latest).
![Step 1](./assets/installation/files1.png)
2. If not done so, expand the Assets tab.
![Step 2](./assets/installation/files2.png)
3. Download either the `mcdc-0.8.3-beta-package.tar.gz` or the `mcdc-0.8.3-beta-package.zip` file, by clicking on it. Use the `mcdc-0.8.3-beta-package.tar.gz` file, if your server runs Linux/MacOS, and `mcdc-0.8.3-beta-package.zip` if your server runs Windows.
![Step 3](./assets/installation/files3.png)
4. Unpack the contents of the archive.
  - On Windows:
    1. Go to `"C:\Users\YOUR USERNAME\Downloads"`.
    2. Double click on the ZIP-File.
![ZIP File inside](./assets/installation/windows/1.png)
    3. In the toolbar, click on `Compressed Folder Tools`.
![ZIP File toolbar](./assets/installation/windows/2.png)
    4. There, click on `Extract All`.
![Extract the ZIP-File - Preparation](./assets/installation/windows/3.png)
    5. Choose the location, where your Server is. If your followed this Guide, it should be located in `"C:\Users\YOUR USERNAME\Desktop\MCDC Server"` and click on OK.
![Extract dialogue](./assets/installation/windows/4.png)
    6. Then open a command prompt, by Pressing and holding the `windows key` and then pressing the `R` key.
![Run box](./assets/installation/windows/5.png)
    7. Then release all the keys and type `cmd`.
![Run box filled in](./assets/installation/windows/6.png)
    8. Click the ok button.
![Run box OK button](./assets/installation/windows/7.png)
  - On MacOS:
    1. Press and hold the `command key` on your keyboard. Then press and release the `space bar`. Your can not release the command key. The Spotlight-Search should now be opened.
    2. Type `Terminal` and press `enter`.
    3. Enter the following command into your terminal. If you installed the server somewhere else, replace `"${HOME}/Desktop/MCDC Server"` with the Path to your server's main directory.
```bash
tar -xf "${HOME}/Downloads/mcdc-0.8.3-beta-package.tar.gz" --directory "${HOME}/Desktop/MCDC Server"
```
  - On Linux:
    1. Press and hold the `Ctrl` and `Alt` Keys. 
    2. Press and release the `T` key. After that, release all the keys. A new Terminal windows should now be opened.
    3. Enter the following command into your terminal. If you installed the server somewhere else, replace `"${HOME}/Desktop/MCDC Server"` with the Path to your server's main directory.
```bash
tar -xf "${HOME}/Downloads/mcdc-0.8.3-beta-package.tar.gz" --directory "${HOME}/Desktop/MCDC Server"
```
5. Type the following commands into your shell/command prompt (replace `server.jar` with the name of the server jar file you downloaded. e. g. `paper-582.jar`).
```bash
cd
cd Desktop
cd "MCDC Server"
java -jar server.jar nogui
```

## Get a discord bot token
1. Go to [https://discord.com/developers/applications/](https://discord.com/developers/applications).
2. In the top right corner, click on `New Application`.
![Step 1](./assets/token1.png)
3. Give the bot a name and click on `Create`.
![Step 2](./assets/token2.png) ![Step 3](./assets/token3.png)
4. In the left side bar, click on `Bot`.
![Step 4](./assets/token4.png)
5. In the top right corner, click on `Add bot`.
![Step 5](./assets/token5.png)
6. Confirm your actions with `Yes, do it!`.
![Step 6](./assets/token6.png)
7. Now Copy your Discord Bot Token by either clicking on `copy`:
![Step 7 - Copy button](./assets/token7.png)
or clicking on `Click to Reveal Token` and then mark your Token and copy it.
![Step 7 - Revealing the Token](./assets/token8-1.png) ![Step 7 - copy the token](./assets/token8-2.png)
