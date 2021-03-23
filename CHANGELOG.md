# Changelog
## Configuration command support!
### v0.6-beta
➕ Added "config" command
↳ "config reload": reloads the config from the file
↳ "config save": saves the current state of the configuration to the file
↳ "config default": restores the cached config to the defaults

---

## Channel Creation update!
### v0.5-beta 
➕ Added Channel creation support: <br>
➕ Added Channel [CreateChannelListener.java](./src/main/java/com/github/mafelp/discord/commands/CreateChannelListener.java) <br>
↳ This allows you to send a message containing `.createChannel <name>` to a discord server and the bot creates the according channels <br>
↳ You are being sent a help message on wrong usage. <br>
↳ You are being sent a success message on success. <br>
↳ A welcome message is sent to the new channel. <br>
➕ Further implemented the new [Logging system](./src/main/java/com/github/mafelp/Logging.java) <br>
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
➕ Added full Javadoc documentation in [doc](./doc) <br>
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