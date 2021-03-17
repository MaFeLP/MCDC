# Changelog
## v0.3.3-beta
### The changelog is here!
➕ Added this changelog
➕ Added all features prior this release into this.

---

## v0.3.2-beta
### 🐞 Bug fix
🐞 Fixed bug in DiscordMain.init() where no token would cause an exception to trigger. It now returns, and the plugin doesn't crash anymore.

---

## v0.3.1-beta
### 🏁 Commenting update!
➕ Added building instructions with Java 8
➕ Added full Javadoc documentation in doc/
➕ Added full commentary to the full code base
➕ Added RoleAdmin.java to prepare for Role management in coming builds

---

## v0.3-beta
### 🏁 MCDC working 
➕ Functions now represents the current status of the project.
➕ Added broadcasting of messages to discord channels defined in /plugins/MCDC/config.yml For more information refer to the README
➕ Added safety to Discord Messages: when this bot sends a message, it is being ignored.

---

## v0.2.1-beta
### DC to MC now stable
➕ Added configuration File support
➕ added CommandExecutor for /token in Token.java
➕ you can use /token <Token> as OP or token <token> in the console to set your Discord bot API Token. This can be found on https://discord.com/developer/applications
🐞 Fixed Bug in Main.onEnable(): Server was not able to fetch token from config.yml

---

## v0.2-beta
### README, building from source and config files!
🐞 Fixed bug, where the config would not be read, which caused the plugin to crash.
➕ Fixed Issues in/Added README.md and bash-scripts/build.sh
🖥 Replaced static version strings with variable Settings.version
🖥 Added non-static API-Token support
 - added CommandExecutor for /token in Token.java
 - you can use "/token <Token>" as OP or "token <token>" in the console to set your Discord bot API Token. This can be found on  https://discord.com/developer/applications
➕ Added configuration File support

---

## v0.1-beta
⛏ Minecraft side of things:
  🗡 Added plugin capability
  🗡️ Members of a discord server, where the bot is invited can send messages to a text channel. These are getting broadcasted to every online player. 
🔊 Discord side of things:
  🔉 The plugin connects as discord bot
  🔉 You can send messages to any channel and the appear on the server

---

What are you looking for, down here?
There was no version prior to [v0.1-beta](#v01-beta)...