# MCDC
A [Minecraft](https://www.minecraft.net) plugin for [paper servers](https://papermc.io).

## Functions
The bot can currently do all the checked items, unchecked will be implemented in the future.
 - [X] Display discord messages in the minecraft chat
    - [X] Discord messages can be sent to the bot via direct message
    - [X] Discord messages can be sent to any server channel the bot is present on
    <br><br>
 - [ ] Display minecraft messages in a discord chat
 - [ ] whisper between a discord user and a minecraft user
 - [ ] linking between a discord and a minecraft account
 - [ ] managing a "#mincraft-server" channel on a specific discord server
   - [ ] this includes that only members with a role can see this channel and write in it
<div class="alert alert-danger" role="alert">Remember that this plugin is currently in its beta phase!<br>
All the functionalities will be added in the future!</div>

## Installation {#Installation}
1. Download the latest [release](https://github.com/MaFeLP/MCDC/releases/) and put it into `<your server directory>/plugins`.
2. Restart the server.
3. Create a new discord bot [here](https://discord.com/developers/applications).
    1. Log in to your discord account
    2. Click on `New Application` in the top right corner.
    3. Give you application a name and click `Create`
    4. Go to the side bar on the left and click `Bot`
    5. Click `Copy` on the right-hand side underneath `Token` and `Click to reveal token`.
4. Go into the console of your server and type `token <your discord bot token>`

---

## Configuration
### \<server directory\>/plugins/MCDC/config.yml:
#### Default config:
```yaml
apiToken: <Your API Token goes here. See Installation>  # string
useShortMsgFormat: true                                 # boolean
```
<div class="alert alert-info" role="alert">Coming in version beta 0.2!</div>

---

## Building from source
1. Install the following dependencies:
   - [maven](https://maven.apache.org/download.cgi)
   - [JavaSE 16](https://www.oracle.com/java/technologies/javase-downloads.html)
   - or [java 8](https://java.com/en/download/) (Needs further configuration. See use with java8)
   - [git](https://git-scm.com/downloads)
2. Open a shell and paste the following command in
```bash
git clone https://github.com/MaFeLP/MCDC.git
cd MCDC/
```

3. In this shell run:
### Automated in macOS and Linux:
```bash
sh -c "./bash-scripts/build.sh"
```
### On Windows, (macOS and Linux: not automated):
```bash
mvn clean
mvn validate
mvn compile
mvn test
mvn package
mvn verify
```
4. Copy the file `MCDC/target/mcdc-VERSION.jar` into your servers plugin folder.
5. Continue with [Installation](#Installation)