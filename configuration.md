# MCDC Configuration
## General
Where to put the file: `<your server directory>/plugins/MCDC/config.yml`

## Types
| Type | Description | Example |
|-----------|--------|-----|
| Boolean | Either `true` or `false` | `true` |
| String | character(s) that are in a line to get a chain of characters | `Hello World` |
| String list | a list of Strings seperated by new lines, two spaces and a `-` | `list:` <br> `  - hello world` <br> `- please star this repository` |
| Short | A number between `-32768` and `32767` | `1` |
| Integer | A number between `-2147483648` and `2147483647` | `1234` |
| long | a number between `-2147483648` and `2147483647`. | `123457890` |
| long list | a list of longs seperated by new lines, two spaces and a `-` | `list:` <br> `  - 1234` <br> `- 1234567890` |

---

## Options:
| Option | Type | Default Value | Description |
|--------|---------|---------|----------|
| useShortMessageFormat | Boolean | `false` | If the minecraft message should not contain additional information about the message, such as the server and channel the message was sent to |
| pluginPrefix | String | `§8[§6MCDC§8]§0: §r` | The prefix before every log entry and other messages sent by the plugin. |
| serverName | String | `A Minecraft Server` | The name of ther server displayed in discord messages |
| debug | Boolean | `false` | If additional information should be displayed. <br> Can be helpful for developers and debugging, but also spams your log files. |
| apiToken | String | Must be filled in before usage | The Token used to create and identify the bot on discord. See [here](./Installation) for more information! |
| discordCommandPrefix | String | `.` | The String used before discord commands, to identify messages as commands and treat them as such. |
| channelIDs | long list | none | The IDs of the channels to broadcast messages to. |
| permission.configEdit.level | Integer | 3 | The OP level defined in `ops.json` that the player must have to execute the command `/config`. <br> Setting it to 0 or lower enables the command for everyone. <br> Setting it to 5 or higher disables it for every player. The console can still execute this command. |
| permission.configEdit.allowedUserUUIDs | String list | none | The UUIDs of players who can execute the `/config` command, and do not have the required OP level. This is also known as a wildcard. |
| permission.discordServerAdmin.allowedUserIDs | long list | none | The list of IDs of discord users who should be allowed to create new channel and roles for MCDC to use. |
| permission.discordBotAdmin.allowedUserIDs | long list | none | The list of IDs of discord users who should be allowed to change settings of the bot (not implemented yet). |
| saveEscapeCharacterInConfig | Boolean | true | Decides if escape characters should be skipped when typing arguments (they will keep their dunctionality tho) or if they should be added to the string. <br> Example: The command `/config set test "foo \" bar"` results in the outputs: <br> if true: `test: foo \" bar"` <br> if false: `test: foo " bar`. |

---

## Default Configuration
```yml
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

# If the command parser should treat \ as a normal character
# Allowed values: <true|false>
saveEscapeCharacterInConfig: true
```

---

Navigation:
Go to [the top of the page](#General) or [back to the main page](./index)
