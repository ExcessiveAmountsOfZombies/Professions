# Profession Data Rework

Previously, all operations that an occupation could have would be stored in the occupation.json file.
This included all the actions, the unlocks, milestones, and data that makes up the occupation.

Operation data will be moved away from the <occupation>.json file to help make everything more
configurable.

## Data will be displayed as follows

**Objects** are considered items that you take an action on, Items, Entities, Blocks are all
considered objects.


* `data/<nmspace>/professions/occupations` All occupation metadata goes in here.
* `data/<nmspace>/professions/operations` All operation data goes in here. 

As we go down this next list, it goes from most generic, to most specific. The most specific data is
applied last, so if something is defined in the class folder, if you specifically define it in the object
folder, it will use the object definition rather than the class or tag definition.

* `/operations/classes/` Any data that goes in this folder, will operate on a class level. 
There are some prebuilt classes, and mod developers will be able to add their own as well.
* `/operations/tags/` If you want to operate on the tag level, you would place entries in this folder.
* `/operations/objects/<type>` Each folder will house a different type of, whether it's blocks,
items, biomes, or something else.

