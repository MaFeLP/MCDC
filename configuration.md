# MCDC Configuration
## General
Where to put the file: <code>your server directory/plugins/MCDC/config.yml</code>

## Types
<table>
    <tr>
        <th>Type</th>
        <th>Description</th>
        <th>Example</th>
    </tr>
    <tr>
        <td>Boolean</td>
        <td>Either <code>true</code> or <code>false</code></td>
        <td><code>true</code></td>
    </tr>
    <tr>
        <td>String</td>
        <td>character(s) that are in a line to get a chain of characters</td>
        <td><code>Hello World</code></td>
    </tr>
    <tr>
        <td>String list</td>
        <td>a list of Strings seperated by new lines, two spaces and a <code>-</code></td>
        <td><code>list:</code> <br> <code>  - hello world</code> <br> <code>- please star this repository</code></td>
    </tr>
    <tr>
        <td>Short</td>
        <td>A number between <code>-32768</code> and <code>32767</code></td>
        <td><code>1</code></td>
    </tr>
    <tr>
        <td>Integer</td>
        <td>A number between <code>-2147483648</code> and <code>2147483647</code></td>
        <td><code>1234</code></td>
    </tr>
    <tr>
        <td>long</td>
        <td>a number between <code>-2147483648</code> and <code>2147483647</code>.</td>
        <td><code>123457890</code></td>
    </tr>
    <tr>
        <td>long list</td>
        <td>a list of longs seperated by new lines, two spaces and a <code>-</code></td>
        <td><code>list:</code> <br> <code>  - 1234</code> <br> <code>- 1234567890</code></td>
    </tr>
</table>

---

## Options:
<table>
    <tr>
        <th>Option</th>
        <th>Type</th>
        <th>Default Value</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>useShortMessageFormat</td>
        <td>Boolean</td>
        <td><code>false</code></td>
        <td>If the minecraft message should not contain additional information about the message, such as the server and channel the message was sent to</td>
    </tr>
    <tr>
        <td>pluginPrefix</td>
        <td>String</td>
        <td><code>§8[§6MCDC§8]§0: §r</code></td>
        <td>The prefix before every log entry and other messages sent by the plugin.</td>
    </tr>
    <tr>
        <td>serverName</td>
        <td>String</td>
        <td><code>A Minecraft Server</code></td>
        <td>The name of ther server displayed in discord messages</td>
    </tr>
    <tr>
        <td>debug</td>
        <td>Boolean</td>
        <td><code>false</code></td>
        <td>If additional information should be displayed. <br> Can be helpful for developers and debugging, but also spams your log files.</td>
    </tr>
    <tr>
        <td>apiToken</td>
        <td>String</td>
        <td>Must be filled in before usage</td>
        <td>The Token used to create and identify the bot on discord. See [here](./Installation) for more information!</td>
    </tr>
    <tr>
        <td>discordCommandPrefix</td>
        <td>String</td>
        <td><code>.</code></td>
        <td>The String used before discord commands, to identify messages as commands and treat them as such.</td>
    </tr>
    <tr>
        <td>channelIDs</td>
        <td>long list</td>
        <td>none</td>
        <td>The IDs of the channels to broadcast messages to.</td>
    </tr>
    <tr>
        <td>permission<br>&nbsp;&nbsp;.configEdit<br>&nbsp;&nbsp;&nbsp;&nbsp;.level</td>
        <td>Integer</td>
        <td><code>3</code></td>
        <td>The OP level defined in <code>ops.json</code> that the player must have to execute the command <code>/config</code>. <br> Setting it to 0 or lower enables the command for everyone. <br> Setting it to 5 or higher disables it for every player. The console can still execute this command.</td>
    </tr>
    <tr>
        <td>permission<br>&nbsp;&nbsp;.configEdit<br>&nbsp;&nbsp;&nbsp;&nbsp;.allowedUserUUIDs</td>
        <td>String list</td>
        <td>none</td>
        <td>The UUIDs of players who can execute the <code>/config</code> command, and do not have the required OP level. This is also known as a wildcard.</td>
    </tr>
    <tr>
        <td>permission<br>&nbsp;&nbsp;.discordServerAdmin<br>&nbsp;&nbsp;&nbsp;&nbsp;.allowedUserIDs</td>
        <td>long list</td>
        <td>none</td>
        <td>The list of IDs of discord users who should be allowed to create new channel and roles for MCDC to use.</td>
    </tr>
    <tr>
        <td>permission<br>&nbsp;&nbsp;.discordBotAdmin<br>&nbsp;&nbsp;&nbsp;&nbsp;.allowedUserIDs</td>
        <td>long list</td>
        <td>none</td>
        <td>The list of IDs of discord users who should be allowed to change settings of the bot (not implemented yet).</td>
    </tr>
    <tr>
        <td>saveEscapeCharacterInConfig</td>
        <td>Boolean</td>
        <td><code>true</code></td>
        <td>Decides if escape characters should be skipped when typing arguments (they will keep their dunctionality tho) or if they should be added to the string. <br> Example: The command <code>/config set test "foo \" bar"</code> results in the outputs: <br> if true: <code>test: foo \" bar"</code> <br> if false: <code>test: foo " bar</code>.</td>
    </tr>
</table>

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
