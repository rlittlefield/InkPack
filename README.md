InkPack
=======

Minecraft plugin for tracking who stole your junk

The idea is that you can craft (currently with a command called /inkpackcreate) a special ink sac that pops when you break a containing chest or kill the player holding it. All items then get a special "Inked" lore, and those items can then be tracked with another special command called /inkpack [id], or by standing in the chunk it popped in, and doing /inkpack with no id parameter. It will list all the items and who or what is currently holding it (or held it last).

TODO:

1. Permissions to prevent everyone from getting the fancy commands.
2. Some properly configurable way to add the "InkPack" lore to the ink sac. (Look into FactoryMod for this)
3. Add some way to remove the Inked lore (an /inkpackclean command)
4. Add an expiration on the ink that will cause it to self-clean when next moved
5. /inkpack command range limit
6. Saving/loading into a text file so things work between server loads.
7. Figuring out why the ink pack lore isn't recognized after server restart
6. Config file for setting range limits and expiration times.
