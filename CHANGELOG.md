# Changelog
## Tab Completions
### v0.11.0-beta
➕ Added Tab Completion to all Minecraft commands

---

## Slash Commands Update
### v0.10.0
➕ Added Slash Commands!<br>
↳ Now use discord commands by using a slash (/) instead of the old discordCommandPrefix Entry in the config file <br><br>

**⚠️ Information about slash commands⚠️**<br>
 - It can take **up to** an hour until the slash commands are registered
   - This is a caching limitation put in place by discord.
 - If you do not see any slash commands after this hour, please see your console log for a link!
 - You **CAN NOT** use normal message commands like `.setup` anymore. Use `/setup` instead, if you are the discord server **owner** and/or the bot owner.
<br>
🚫 Removed the old discord commands, that could be used in messages.

---

## BIG Account update
### v0.9.0
➕ Added Accounts! <br>
↳ Place an account Tag in your minecraft message, and it will be replaced with a discord ping (if the user has a linked account)! <br>
↳ Added a new minecraft command `/link`: Link your minecraft and discord accounts! <br>
↳ Added a new minecraft command `/unlink`: Unlink your minecraft and discord accounts! <br>
↳ Added a new minecraft command `/account`: Manage your accounts! <br>
↳ Added a new minecraft command `/whisper`: Whisper to a friend on discord! <br>
↳ Added a new minecraft command `/dcmsg`: Whisper to a friend on discord! <br>
↳ Added a new discord command `<prefix>link`: Link your discord to your minecraft! <br>
↳ Added a new discord command `<prefix>whisper`: Whisper your message to a minecraft player! <br>
↳ Added a new discord command `<prefix>mcmsg`: Whisper your message to a minecraft player! <br>
➕ Added some more toggles: <br>
↳ The bot **can** send message with server commands and player commands to all the channels. <br>
↳ Toggle, if the footer should be displayed. <br>
↳ Toggle, if discord command messages should be deleted b the bot. <br>

---

## Small bug fixes
### v0.8.4-beta
➕ Added Error message, when then bot does not have the required permissions, to create Channels or/and roles (See issue [29](https://github.com/MaFeLP/MCDC/issues/29)). <br>
➕ Added threading to the bot, where the logging in and message sending are going to be handled off the main thread and should not lag the server. <br>

---

## More Bug fixes
### v0.8.2-beta and v0.8.3-beta
➕ Added permission checking to the commands `/token`, `/config`,`.setup`,`.createChannel` and `.createRole`. <br>
➕ Added differentiation of `'` and `"` in arguments. You can now use commands like this: `hello "mafelp's friend!"` with no problems! <br>
➕ Added escape sequences for quotation marks.
↳ Added a toggle in the configuration to change, if these marks should be displayed in the config as well. <br>
🐞 Fixed a bug, where giving an empty argument ("") would cause an error and the user would not be notified. <br>
🐞 Fixed a bug, where setting up or creating channels with an 'empty' argument ("") would cause an error to occure and the user would not be notified about the error. <br>
🐞 Fixed a bug, where the long message format would not display corrently in the console and would display in two lines. <br>
🐞 Fixed a bug where an uneven amount of quotation marks (`'` and `"`) would cause a CommandNotFinishedException and the user would not be notified. <br>

---

## Bug Fix update
### v0.8.1-beta
🐞 Fixed a bug in the console, where there would an error be shown, when the command was executed successfully. <br>
🐞 Fixed a bug in the setup command where two replies would be sent.

---

## Setup Update
### v0.8.0-beta
➕ Added display of the bot invite link on enable of the bot. <br>
➕ Added discord command `<prefix>setup <name>` which creates a new channel called `<name>` and a new role, also called `<name>`. 

---

## Permission Update!
### v0.7.1-beta
➕ Added Permission support! <br>
↳ Added long lists `permission.botAdminIDs` for bot Admin and `permission.adminIDs` for server admin commands. <br>
↳ You now need to have one of the following permissions to execute admin commands on the discord server:
 - config.yml:
   - permission.adminIDs
 - be bot owner
 - be server admin
➕ You can now configure commands in this plugin to require a specific OP level in the server's `ops.json` for executing commands.

---

## Role Creation Update!
### v0.7.0-beta
➕ Added Role creation support <br>
↳ The Role is being created in any discord server chat with the command `<prefix>createRole <name>` <br>
⚠️The role can currently be created by everyone with write access to a server's channel!

---

## Config, command parsing and more!
### v0.6.4-beta
📃 The project is now licensed under the GPLv3 license. View [the LICENSE file](./LICENSE) for more info. <br>
⤵ Further implemented the new command parsing algorithm. <br>
↳ Fixed all known bugs from the previous version. See the commits for more information! <br>
↳ Moved some stuff around in the backend. <br>
➕ Added the functionality for the subcommands `set`, `get`, `add` and `remove` to the `/config` command. <br>
✏ ️Fixed a visual bug where the prefix would be shown twice on instance startup.

---

## Small bug fix update
### v0.6.3-beta
🛠 Updated [Config.java](./src/main/java/com/github/mafelp/minecraft/commands/Config.java): <br>
↳ Added subcommands `set`, `get`, `add` and `remove` to the `/config` command. <br>
↳ Updated [plugin.yml](./src/main/resources/plugin.yml) to represent those changes in the usages part. <br>
📗 Updated the javadoc <br>

---

## Back-End update
### v0.6.2-beta
➕ Added command parsing: <br>
↳ ➕ Added Command.java: parses a string into a command and arguments. <br>
↳ ➕ Added CommandNotFinishedException that is being thrown, when the string ends, and a quotation mark marked the beginning of an argument and never ended it. <br>
↳ ➕ Added NoCommandGivenException that is being thrown, when the given String is null or has a length of 0. <br>
➕ Added TestMain.java to debug classes and Methods. <br>
↳ This is required, because the server calls the program, and it is not an independent program. <br>

---

## Small bug fix update
### v0.6.1-beta
🐞 Fixed bug in /token: The command would not set the token and use it.

---

## Configuration command support!
### v0.6-beta
➕ Added "config" command <br>
↳ "config reload": reloads the config from the file <br>
↳ "config save": saves the current state of the configuration to the file <br>
↳ "config default": restores the cached config to the defaults <br>

---

## Channel Creation update!
### v0.5-beta 
➕ Added Channel creation support: <br>
➕ Added Channel [CreateChannelListener.java](./src/main/java/com/github/mafelp/discord/commands/CreateChannelListener.java) <br>
↳ This allows you to send a message containing `.createChannel <name>` to a discord server and the bot creates the according channels <br>
↳ You are being sent a help message on wrong usage. <br>
↳ You are being sent a success message on success. <br>
↳ A welcome message is sent to the new channel. <br>
➕ Further implemented the new [Logging system](./src/main/java/com/github/mafelp/utils/Logging.java) <br>
➕ Added command filter so commands are not sent to the minecraft chat. <br>

---

## v0.4.1-beta
### Skins commenting update
➕ Added commenting and JavaDoc to [Skin.java](./src/main/java/com/github/mafelp/minecraft/skins/Skin.java) <br>
➕ Added commenting and JavaDoc to [SkinManager.java](./src/main/java/com/github/mafelp/minecraft/skins/SkinManager.java) <br>
➕ Added new configuration variable info `config.yml`: `debug` <br> 
↳ Displays additional information <br>
▷◁ Merged Settings.skinFileDirectory into Settings.configurationFileDirectory <br>

---

## v0.4-beta
### Skins update!
➕ Added package [com.github.mafelp.minecraft.skins](./src/main/java/com/github/mafelp/minecraft/skins)<br>
➕ Added [Skin.java](./src/main/java/com/github/mafelp/minecraft/skins/Skin.java) <br>
↳ Class to store information about the Skin of a player in, like the skin and head file.<br>
➕ Added [SkinManager.java](./src/main/java/com/github/mafelp/minecraft/skins/SkinManager.java) <br>
↳ Class to handle all skin downloading and storing for the Skin class.<br>
➕ Added downloading of the skin on join. <br>
➕ Added display of the head in the head of the discord message embed.

---

## v0.3.4-beta
### Changelog fixes
🛠 Fixed some rendering bugs in the [changelog](./CHANGELOG.md).

---

## v0.3.3-beta
### The changelog is here!
➕ Added this changelog<br>
➕ Added all features prior this release into this.

---

## v0.3.2-beta
### 🐞 Bug fix
🐞 Fixed bug in [DiscordMain.init()](./src/main/java/com/github/mafelp/discord/DiscordMain.java) where no token would cause an exception to trigger. It now returns, and the plugin doesn't crash anymore.

---

## v0.3.1-beta
### 🏁 Commenting update!
➕ Added building instructions with Java 8 <br>
➕ Added full Javadoc documentation in [doc](https://mafelp.github.io/MCDC/doc/development/index.html) <br>
➕ Added full commentary to the full code base<br>
➕ Added [RoleAdmin.java](./src/main/java/com/github/mafelp/discord/RoleAdmin.java) to prepare for Role management in coming builds

---

## v0.3-beta
### 🏁 MCDC working 
➕ Functions now represents the current status of the project.<br>
➕ Added broadcasting of messages to discord channels defined in `<your sever folder>/plugins/MCDC/config.yml` For more information refer to the [README](./README.md)<br>
➕ Added safety to Discord Messages: when this bot sends a message, it is being ignored.

---

## v0.2.1-beta
### DC to MC now stable
➕ Added configuration File support<br>
➕ added CommandExecutor for /token in [Token.java](./src/main/java/com/github/mafelp/minecraft/commands/Token.java) <br>
➕ you can use `/token <Token>` as OP or `token <token>` in the console to set your Discord bot API Token. This can be found on https://discord.com/developer/applications <br>
🐞 Fixed Bug in [Main.onEnable()](./src/main/java/com/github/mafelp/minecraft/Main.java): Server was not able to fetch token from config.yml

---

## v0.2-beta
### README, building from source and config files!
🐞 Fixed bug, where the config would not be read, which caused the plugin to crash.<br>
➕ Fixed Issues in/Added [README.md](./README.md) and [bash-scripts/build.sh](./bash-scripts/build.sh) <br>
🖥 Replaced static version strings with variable in Settings.version<br>
🖥 Added non-static API-Token support<br>
➕ added CommandExecutor for `/token` in [Token.java](./src/main/java/com/github/mafelp/minecraft/commands/Token.java) <br>
🖱️ you can use `/token <Token>` as OP or `token <token>` in the console to set your Discord bot API Token. This can be found on  https://discord.com/developer/applications <br>
➕ Added configuration File support

---

## v0.1-beta
🗡 Added plugin capability<br>
🗡️ Members of a discord server, where the bot is invited can send messages to a text channel. These are getting broadcast to every online player.<br>
🔊 The plugin connects as discord bot<br>
🔊 You can send messages to any channel and that appear on the server<br>

---

What are you looking for, down here?<br>
There was no version prior to [v0.1-beta](#v01-beta)...<br>
[Go back up. Fast! You better use the latest version 😉](#changelog)
