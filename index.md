# MCDC
A [Minecraft](https://www.minecraft.net) plugin for [paper servers](https://papermc.io).

## Functions
The bot can currently do all the checked items, unchecked will be implemented in the future.
 - [X] Display discord messages in the minecraft chat
    - [X] Discord messages can be sent to the bot via direct message
    - [X] Discord messages can be sent to any server channel the bot is present on
 - [X] Display minecraft messages in a discord chat
 - [X] managing a "#mincraft-server" channel on a specific discord server
   - [ ] this includes that only members with a role can see this channel and write in it
 <br><br>
 - [ ] whisper between a discord user and a minecraft user
 - [ ] linking between a discord and a minecraft account
<div class="alert alert-danger" role="alert">Remember that this plugin is currently in its beta phase!<br>
All the functionalities will be added in the future!</div>

## Installation
1. Download the latest [release](https://github.com/MaFeLP/MCDC/releases/) and put it into `<your server directory>/plugins`.
2. Restart the server.
3. Create a new discord bot [here](https://discord.com/developers/applications).
    1. Log in to your discord account
    2. Click on `New Application` in the top right corner.
    3. Give you application a name and click `Create`
    4. Go to the sidebar on the left and click `Bot`
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

# Discord Channel IDs to broadcast messages to.
channelIDs:
   - 1234


# Permission section for setting permission levels
permission:

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
5. Continue with [Installation](#installation)

## Documentation
The documentation cna be found either on
[my main project page](https://mafelp.github.io/documentation/MCDC/doc/development/index.html)
or [on this project's page](./doc/index.html).