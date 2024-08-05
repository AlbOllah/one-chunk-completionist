<?php

$uniq = [];
$json = json_decode(file_get_contents('challenges-combat.json'), true);
//foreach($json as $k1 => $v1) {
	foreach($json as $k2 => $v2) {
		foreach($v2 as $k => $v) {
			if(!isset($uniq[$k])) {
				$uniq[$k] = $v;
			}
		}
	}
//}

var_dump($uniq);

// Skill
// - Category
// - Objects
// - Level
// - Primary
// - Priority
// - NoPet
// - Chunks
// - Tasks
// - Items
// - NoBoost
// - Skills
// - Output
// - NPCs
// - NotF2P
// - NotEquip
// - AlwaysValid
// - OutputObject
// - NoXp
// - Monsters
// - BackupParent
// - ManualInvalid
// - ManualNonProcessing
// - Source
// - Mix

// Quest
// - BaseQuest
// - Description
// - Chunks
// - NPCs
// - QuestPointsNeeded
// - Tasks
// - Items
// - QuestPoints
// - XpReward
// - Reward
// - Monsters
// - Skills
// - NotF2P
// - NoBoost
// - Objects
// - SkillsBoost
// - KudosNeeded

// NonSkill
// - Monsters
// - OutputObject
// - NotF2P
// - Items
// - Output
// - NPCs
// - Objects
// - Chunks
// - InfoLink
// - Source
// - Sections
// - ConnectsSections
// - UnlocksArea
// - Description
// - Skills
// - XpReward
// - Category
// - QuestPointsNeeded
// - TotalLevelNeeded
// - CombatLevelNeeded
// - Reward
// - Kudos
// - ClueTier
// - ClueType
// - NotSkiller
// - StarRegion

// Extra
// - Category
// - Label
// - Items
// - NotF2P
// - Requirements
// - Tasks
// - Priority
// - Set
// - NonShop
// - Chunks
// - Monsters
// - Output
// - QuestPointsNeeded
// - Objects
// - NPCs
// - NotSkiller
// - Skills

// Diary
// - Description
// - NPCs
// - Tasks
// - BaseQuest
// - Chunks
// - Objects
// - Skills
// - Items
// - Reward
// - XpReward
// - ManualShow
// - NoBoost
// - SkillsBoost
// - Monsters
// - NotSkiller
// - Category
// - QuestPointsNeeded
// - KudosNeeded
// - CombatPoints
// - CombatPointsNeeded

// Combat
// - Chunks
// - Level
// - Primary
// - Priority
// - NotF2P
// - Category
// - NPCs
// - Slayer
// - Skills
// - Tasks
