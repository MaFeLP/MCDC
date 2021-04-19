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

---

## Installation
See [Installation](./installation) for detailed installation instructions with pictures.

### Quick install:
1. Download the latest [release](https://github.com/MaFeLP/MCDC/releases/) and put it into `<your server directory>/plugins`.
2. Restart the server.
3. Create a new discord bot and app [here](https://discord.com/developers/applications).
4. Go into the console of your server and type `token <your discord bot token>` <br>
   OR go into the `<serverDirectory>/plugins/MCDC/config.yml` file and change the value of `apiToken` to your token.
5. Invite the bot to your servers, by clicking on the link in your console. (Instructions [here](./invite-bot))

---

## Commands
### Discord commands
<table>
	<tr>
		<th>Command</th>
		<th>Instructions Link</th>
	</tr>
	<tr>
		<td>setup</td>
		<td><a href="./commands/setup">setup</a></td>
	</tr>
	<tr>
		<td>createChannel</td>
		<td><a href="./commands/create-channel">create-channel</a></td>
	</tr>
	<tr>
		<td>createRole</td>
		<td><a href="./commands/create-role">create-role</a></td>
	</tr>
</table>

## Minecraft commands
<table>
	<tr>
		<th>Command</th>
		<th>Instructions Link</th>
	</tr>
	<tr>
		<td>token</td>
		<td><a href="./commands/token">token</a></td>
	</tr>
	<tr>
		<td>config</td>
		<td><a href="./commands/config">config</a></td>
	</tr>
	<tr>
		<td>link</td>
		<td><a href="./commands/link">link</a></td>
	</tr>
</table>

---

## Configuration
### \<server directory\>/plugins/MCDC/config.yml:
See [configuration](./configuration) for more information.

---

### Get the ID of a text channel:
See [get the channel ID](./get-channel-ID) for information on this.

---

## Building from source
See [Building from Source](./building-from-source) for information, on how to do this.

---

## Documentation
The documentation cna be found either on
[my main project page](https://mafelp.github.io/documentation/MCDC/doc/development/index.html)
or [on this project's page](./doc/development/index.html).
