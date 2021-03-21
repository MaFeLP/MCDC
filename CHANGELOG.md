# Changelog
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