# MCDC
A [Minecraft](https://www.minecraft.net) plugin for [paper servers](https://papermc.io).

## Features
The bot can currently do all the checked items, unchecked will be implemented in the future.
 - [X] Display discord messages in the minecraft chat
    - [X] Discord messages can be sent to the bot via direct message
    - [X] Discord messages can be sent to any server channel the bot is present on
 - [X] Display minecraft messages in a discord chat
 - [X] managing a "#mincraft-server" channel on a specific discord server
   - [X] this includes that only members with a role can see this channel and write in it
 - [X] whisper between a discord user and a minecraft user
 - [X] linking between a discord and a minecraft account
 - [X] Toggle-able: Sending minecraft commands to the discord chats.
 - [X] Slash Commands: use slash commands in the discord chat.
 - [X] Tab completion in Minecraft
<br><br>
 - [ ] A message in a channel that displays all online Members.
 - [ ] Migrate more slash commands to discord
   - [ ] `/account`
   - [ ] `/config`
 - [ ] Add a new command: `/help`
   - [X] `/help` in minecraft
   - [ ] `/help` in discord

## Installation
1. Download the latest [release](https://github.com/MaFeLP/MCDC/releases/) and put it into `<your server directory>/plugins`.
2. Restart the server.
3. Create a new discord bot [here](https://discord.com/developers/applications).
    1. Log in to your discord account
    2. Click on `New Application` in the top right corner.
    3. Give you application a name and click `Create`
    4. Go to the side bar on the left and click `Bot`
    5. Click `Copy` on the right-hand side underneath `Token` and `Click to reveal token`.
4. Go into the console of your server and type `token <your discord bot token>` <br>
   OR go into the `<serverDirectory>/plugins/MCDC/config.yml` file and change the value of `apiToken` to your token.

---

## Configuration
### \<server directory\>/plugins/MCDC/config.yml:
#### Default config:
```yaml
# Configuration file for plugin MCDC
# Author: MaFeLP (https://github.com/MaFeLP/MCDC/)

# if the message should be shortened
# Allowed values: <true|false>
useShortMessageFormat: false

# the prefix displayed in the console before logs and in
# Allowed values: any String
pluginPrefix: '§8[§6MCDC§8]§0: §r'

# The name of the server displayed in discord messages
# Allowed values: any string
serverName: 'A Minecraft Server'

# If additional information should be displayed.
# Allowed values: <true|false>
debug: false

# The Token used to create your bot instance
# Allowed values: any String
apiToken: 'Your API Token goes here!'

# The String used before commands in the discord channels
# Allowed values: any String
discordCommandPrefix: '.'

# Selects if messages that are commands should be deleted after execution.
# Allowed values: <true|false>
deleteDiscordCommandMessages: false

# Discord Channel IDs to broadcast messages to.
channelIDs:
   - 1234

# Enables accounts and linking.
# Allowed values <true|false>
enableLinking: true

# Allow players to list all the accounts.
# Allowed values <true|false>
allowListAllAccounts: true

# Decides, if the config value 'serverName' should be displayed in the footer of discord messages.
# Allowed values <true|false>
showFooterInMessages: true

# The value that should be displayed below the name
activity:
   # If the bot should have an activity.
   enabled: true
   # The type of the message, aka. the first word:
   # can be set to custom, competing, listening, watching, streaming or playing
   type: listening
   # The text that should be displayed.
   message: "your messages 👀"

# If the bot should send a message to the listening channels, if a command was executed by ...
sendCommandToDiscord:
   # ... a player.
   player: false
   # ... the server.
   server: false

# Permission section for setting permission levels
permission:
   # The permissions on linking and editing accounts.
   accountEdit:
      # The OP level needed to remove accounts of players.
      level: 3
      # A list of UUIDs of Players who have a wildcard to use this command.
      allowedUserUUIDs:
         - a unique ID

   # Permission for minecraft command /config
   configEdit:
      # Required OP level
      level: 3
      # A list of UUIDs of Players who have a wildcard to use this command.
      allowedUserUUIDs:
         - a unique ID


   # Discord Server Admins are allowed to create Channels and Roles
   discordServerAdmin:
      # A list of discord IDs of users who have a wildcard to use this command
      allowedUserIDs:
         - 1234

   # Discord Bot admins, use cases may follow.
   discordBotAdmin:
      # A list of authorised bot users.
      allowedUserIDs:
         - 1234

# If the command parser should treat \ as a normal character
# Allowed values: <true|false>
saveEscapeCharacterInConfig: true
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
