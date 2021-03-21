# MCDC
A [Minecraft](https://www.minecraft.net) plugin for [paper servers](https://papermc.io).

## Functions
The bot can currently do all the checked items, unchecked will be implemented in the future.
 - [X] Display discord messages in the minecraft chat
    - [X] Discord messages can be sent to the bot via direct message
    - [X] Discord messages can be sent to any server channel the bot is present on
 - [X] Display minecraft messages in a discord chat
 <br><br>
 - [ ] whisper between a discord user and a minecraft user
 - [ ] linking between a discord and a minecraft account
 - [ ] managing a "#mincraft-server" channel on a specific discord server
   - [ ] this includes that only members with a role can see this channel and write in it
<div class="alert alert-danger" role="alert">Remember that this plugin is currently in its beta phase!<br>
All the functionalities will be added in the future!</div>

## Installation
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
apiToken: <Your API Token goes here. See Installation>      # string
useShortMsgFormat: true                                     # boolean
pluginPrefix: <Formatted Plugin Prefix for console>         # String, optional 
serverName: <A name for your server, displayed in Messages> # String
debug: <should additional information should be displayed>  # boolean
channelIDs:                                                 # long list
  - <ID of your first channel>                              # long
  - <ID of your second channel>                             # long
  - ...
```

### Get the ID of a text channel:
1. In your discord app open the user settings (next to your mute buttons)
2. Go to the tab `Appearence`
3. Scroll down and in the tab `Advanced` check the box `Developer Mode`
4. Close the settings and right click on a text channel. Then click `Copy ID`

---

## Building from source
1. Install the following dependencies:
   - [maven](https://maven.apache.org/download.cgi)
   - [JavaSE 15](https://www.oracle.com/java/technologies/javase-downloads.html)
   - or [java 8](https://java.com/en/download/) (Needs further configuration. See use with java8)
   - [git](https://git-scm.com/downloads)
2. Open a shell and paste the following command in
```bash
git clone https://github.com/MaFeLP/MCDC.git
cd MCDC/
```

#### Using custom java version Java 8:
In pom.xml edit the value for `<java.version>`:
##### Normal:
```xml
<project>
    <properties>
        <java.version>15</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
   ...
</project>
```
##### Using java 8:
```xml
<project>
    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
   ...
</project>
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
5. Continue with [Installation](#installation)
