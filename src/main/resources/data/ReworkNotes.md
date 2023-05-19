# Profession Data Rework

1.1.0 is a TRANSITION version. It serves no purpose except to add the command `/professions admin convert_new_format` 
This will convert the currently loaded data into the new format, except for enchanting, to be used in 2.0.0. If you don't
have any custom data, skip 1.1.0 and just wait until 2.0.0.

The purpose is to remove the appenders, and make it easier to configure and override things that you don't want.

Previously, all operations that an occupation could have would be stored in the occupation.json file.
This included all the actions, the unlocks, milestones, and data that makes up the occupation.

Operation data will be moved away from the <occupation>.json file to help make everything more
configurable.

## Data will be displayed as follows

**Objects** are considered items that you take an action on, Items, Entities, Blocks are all
considered objects.


* `data/<nmspace>/professions/occupations` All occupation metadata goes in here.
* `data/<nmspace>/professions/operations` All operation data goes in here. 


* `/operations/actionables/<type>`

Each registry that has an applicable action will have a folder, so blocks, biomes, entity_types, items, etc. The tags for
those things will also be placed in those same folders.
