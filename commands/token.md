# [Minecraft]: Token command
## What does it do?
This command enables you to change the token of the discord bot instance and restarts the instance with the new token.

---

## How to use it?
In the console:
```
token "YOUR DISCORD BOT TOKEN"
```

As a player:
```
/token """YOUR DISCORD BOT TOKEN"
```

The token can be found [here](./../installation#get-the-discord-bot-token).

---

## Which permissions are required?
If you execute this command in the console, you do not have to have any permissions, because the console can execute **any** MCDC commands.

### For the user
#### Using OP Level
The user needs to have an OP Level equal or higher than the level specified in the config file: `permission.configEdit.level` Get the value for this, using the command
```
config get permission.configEdit.level
```

in the console. <br><br>
To change the OP level, go to your servers main directory, open the `opsn.json` file and edit the value `"level": 4,`. Here replace the number 4 with the level you want to give the player, whose name is specified in the line above. If your user does not appear in this list, use the command `op USERNAME` in the console, to add the user to the list of OPs.

#### Using Wildcards
A users UUID must be specified in `permission.configEdit.allowedUserUUIDs` for the user to edit the configuration file, without having the required OP level. To add a user to this list, use
```
config add permission.configEdit.allowedUserUUIDs THE_UUID_OF_THE_PLAYER
```

You get the UUID, when a player joins the server in the servers console.
