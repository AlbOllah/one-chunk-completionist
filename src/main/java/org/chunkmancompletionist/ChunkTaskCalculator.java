package org.chunkmancompletionist;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import org.chunkmancompletionist.managers.ChunkTasksManager;
import org.chunkmancompletionist.panel.ChunkmanCompletionistPanel;
import org.chunkmancompletionist.types.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Singleton
@Slf4j
public class ChunkTaskCalculator {
    @Inject
    private ChunkTasksManager manager;

    private EventBus eventBus;

    private ChunkInfo chunkInfo;
    private Map<String, Boolean> chunkIds = new LinkedHashMap<>();

    Map<String, Chunk> chunks = new LinkedHashMap<>();
    private Map<String, String> possibleAreas = new HashMap<>();
    private Map<String, Map<String, Boolean>> areasStructure = new HashMap<>();
    private Map<String, Boolean> manualAreas = new HashMap<>();
    private Map<String, Boolean> randomLoot = new HashMap<>();
    private Map<String, Boolean> manualEquipment = new HashMap<>();
    private Map<String, Map<String, Boolean>> manualSections = new HashMap<>();
    private Map<String, Map<String, Boolean>> backloggedSources = new HashMap<>();
    private int rareDropDivisor = 999999999;
    private Float rareDropNum;
    private int secondaryPrimaryDivisor = 999999999;
    private Float secondaryPrimaryNum;

    private boolean optOutSections = false;

    @Inject
    public ChunkTaskCalculator(EventBus eventBus) {
        this.eventBus = eventBus;
        chunkInfo = loadFromFile("/chunkinfo.json", new TypeToken<>(){});
    }

    @Subscribe
    void calculate(ChunkTaskCalculateMessage event) {
        chunkIds.put("10810", true);
        chunkIds.put("12082", true);

        manualSections.put("9525", Map.of("1", true));
        manualSections.put("10810", Map.of("1", true));
        manualSections.put("12082", Map.of("1", true));

        rareDropNum = (float)(1 / rareDropDivisor);
        secondaryPrimaryNum = (float) (1 / secondaryPrimaryDivisor);

        getAllChunkAreas(chunkIds);
        BaseChunkData baseChunkData = gatherChunksInfo();

        eventBus.post(new ChunkTaskCalculatorMessage("I did some work"));
    }
    private double gcd(double a, double b) {
        if(b < 0.0000001)
            return a;

        return gcd(b, Math.floor(a % b));
    }

    private String findFraction(Double fraction, boolean isRoundedDenominator) {
        int len = String.valueOf(fraction).length() - 2;

        double denominator = Math.pow(10, len);
        double numerator = fraction * denominator;

        double divisor = gcd(numerator, denominator);

        numerator /= divisor;
        denominator /= divisor;
        double v = Double.parseDouble(Math.round(Double.parseDouble(denominator / numerator + "e+2")) + "e-2");
        if(isRoundedDenominator) {
            return String.format("1/%s", String.valueOf(+(Math.floor(v))).replace("/(\\d)(?=(\\d\\d\\d)+(?!\\d))/g", "$1,"));
        } else {
            return String.format("1/%s", String.valueOf(+v).replace("/(\\d)(?=(\\d\\d\\d)+(?!\\d))/g", "$1,"));
        }
    }

    private Map<String, Map<String, Boolean>> findConnectedSections(Map<String, Map<String, Boolean>> sectionsIn) {
        AtomicBoolean added = new AtomicBoolean(false);
        chunkInfo.sections.forEach((chunk, sections) -> {
            if(chunks.containsKey(chunk)) {
                sections.forEach((section, connections) -> {
                    if(!Objects.equals(section, "0") && (sectionsIn.size() == 0 || !sectionsIn.containsKey(chunk) || !sectionsIn.get(chunk).containsKey(section) || !sectionsIn.get(chunk).get(section))) {
                        if(sectionsIn.containsKey(chunk) && sectionsIn.get(chunk).containsKey(section) && !sectionsIn.get(chunk).get(section)) {
                            sectionsIn.get(chunk).remove(section);
                        } else if(manualSections.isEmpty() || !manualSections.containsKey(chunk) || !manualSections.getOrDefault(chunk, new HashMap<>()).containsKey(section) || manualSections.getOrDefault(chunk, new HashMap<>()).getOrDefault(section, false)) {
                            Chunk chunk1 = chunkInfo.chunks.getOrDefault(chunk, null);
                            if(
                                optOutSections ||
                                connections.stream().anyMatch((connection) -> {
                                    return connection.contains("-") ? (sectionsIn.getOrDefault(connection.split("-")[0], new HashMap<>()).containsKey(connection.split("-")[1])) : chunks.containsKey(connection);
                                }) ||
                                chunk1 != null && chunk1.Sections.containsKey(section) && chunk1.Sections.get(section).Connect.entrySet().stream().anyMatch((entry) -> {
                                    return chunkInfo.chunks.containsKey(entry.getKey()) &&
                                        !chunkInfo.chunks.get(entry.getKey()).Name.equals("") &&
                                        chunkIds.containsKey(chunkInfo.chunks.get(entry.getKey()).Name) &&
                                        chunkIds.get(chunkInfo.chunks.get(entry.getKey()).Name) &&
                                        !Objects.equals(chunkInfo.chunks.get(entry.getKey()).Name, "Zanaris");
                                })
                            ) {
                                if(!sectionsIn.containsKey(chunk)) {
                                    sectionsIn.put(chunk, Map.of(section, true));
                                }

                                added.set(true);
                            }
                        }
                    }
                });
            }
        });

        if(added.get()) {
            return findConnectedSections(sectionsIn);
        } else {
            return sectionsIn;
        }
    }

    private BaseChunkData gatherChunksInfo() {
        BaseChunkData chunkData = new BaseChunkData();
        Rules rules = manager.getProfile().rules;
        rules.PuroPuro = true;
        Map<String, Map<String, String>> dropTables = chunkInfo.codeItems.dropTables;
        Map<String, Boolean> bossMonsters = chunkInfo.codeItems.bossMonsters;

        randomLoot.forEach((item, is) -> {
            if(!chunkData.items.containsKey(item)) {
                chunkData.items.put(item, Map.of("Random Event Loot", "secondary-drop"));
            }
        });

        manualEquipment.forEach((item, is) -> {
            if(!chunkData.items.containsKey(item)) {
                chunkData.items.put(item, Map.of("Manually Added Equipment", "secondary-drop"));
            }
        });

        Map<String, Map<String, Boolean>> manualSectionsModified = new HashMap<>();
        manualSections.forEach((chunk, sections) -> {
            sections.forEach((section, is) -> {
                if(chunks.containsKey(chunk)) {
                    manualSectionsModified.put(chunk, manualSections.get(chunk));
                }
            });
        });

        chunkData.unlockedSections.putAll(manualSectionsModified);
        chunkData.unlockedSections.putAll(findConnectedSections(chunkData.unlockedSections));
        log.info(chunkData.unlockedSections.toString());

        chunks.forEach((chunkId, chunk) -> {
            if(!chunk.Sections.isEmpty()) {
                chunk.Sections.entrySet().stream().filter((section)-> chunkData.unlockedSections.getOrDefault(chunkId, new HashMap<>()).getOrDefault(section.getKey(), false)).forEach((entry) -> {
                    String sectionId = entry.getKey();
                    Chunk section = entry.getValue();

                    //log.info(String.format("%s -> %s", sectionId, GSON.toJson(section)));
                    if(rules.PuroPuro || !Objects.equals(chunkId, "Puro-Puro")) {
                        section.Monster.forEach((monster, count) -> {
                            if(!rules.Skiller && chunkInfo.drops.containsKey(monster) && !backloggedSources.getOrDefault("monsters", new HashMap<>()).containsKey(monster)) {
                                chunkInfo.drops.get(monster).forEach((drop, quantities) -> {
                                    //log.info(String.format("%s - %s: ", monster, drop));
                                    quantities.forEach((quantity, chance1) -> {
                                        log.info(String.format("%s - %s", quantity, chance1));
                                        if(dropTables.containsKey(drop) && ((!Objects.equals(drop, "RareDropTable+") && !Objects.equals(drop, "GemDropTable+")) || rules.RDT) && !Objects.equals(drop, "GemDropTableLegends+")) {
                                            dropTables.get(drop).forEach((item, chance2) -> {
                                                if ((Objects.equals(drop, "RareDropTable+") || Objects.equals(drop, "GemDropTable+")) && Objects.equals(item, "Chaos talisman")) {
                                                    return;
                                                }

                                                Float monsterDropChance = chunkInfo.getMonsterDropChance(monster, drop, quantity);
                                                Float dropTableChance = chunkInfo.getDropTableChance(drop, item);

                                                if((rules.RareDrop || monsterDropChance == 0 || dropTableChance == 0 || monsterDropChance * dropTableChance > rareDropNum) && (rules.Boss || bossMonsters.containsKey(monster)) && !backloggedSources.getOrDefault("items", new HashMap<>()).containsKey(item)) {
                                                    if(!chunkData.items.containsKey(item)) {
                                                        chunkData.items.put(item, new HashMap<>());
                                                    }

                                                    if((monsterDropChance == 1 && dropTableChance == 1) || (secondaryPrimaryDivisor > 50 && (monsterDropChance == 0 || dropTableChance == 0)) || monsterDropChance * dropTableChance >= secondaryPrimaryNum) {
                                                        chunkData.items.get(item).put(monster, "primary-drop");
                                                    } else {
                                                        chunkData.items.get(item).put(monster, "secondary-drop");
                                                    }
                                                }

                                                if(!chunkData.dropRates.containsKey(monster)) {
                                                    chunkData.dropRates.put(monster, new HashMap<>());
                                                }
                                                chunkData.dropRates.get(monster).put(item, findFraction((double)(monsterDropChance * dropTableChance), drop.contains("GeneralSeedDropTable")));

                                                if(!chunkData.dropTables.containsKey(monster)) {
                                                    chunkData.dropTables.put(monster, new HashMap<>());
                                                }
                                                if(!chunkData.dropTables.get(monster).containsKey(item)) {
                                                    chunkData.dropTables.get(monster).put(item, new HashMap<>());
                                                }
                                                String calcedQuantity = "0";
                                                String[] split = dropTables.get(drop).get(item).split("@");
                                                if(split.length > 1) {
                                                    String[] split2 = split[1].split(" (noted)");
                                                    String quant = String.valueOf(Integer.parseInt(split2[0]) * Integer.parseInt(quantity));
                                                    if(split2.length > 1) {
                                                        calcedQuantity =  quant + split2[1];
                                                    } else {
                                                        calcedQuantity = quant;
                                                    }
                                                }
                                                chunkData.dropTables.get(monster).get(item).put(calcedQuantity, findFraction((double)monsterDropChance * dropTableChance, drop.contains("GeneralSeedDropTable")));
                                            });
                                        }
                                    });
                                });
                            }
                        });
                    }
//                    else if (
//                    (
//                            rules['Rare Drop'] ||
//                                    isNaN(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) ||
//                                    (
//                                            parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) /
//                                                    parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])
//                                    ) > (
//                                            parseFloat(rareDropNum.split('/')[0].replaceAll('~', '')) /
//                                                    parseFloat(rareDropNum.split('/')[1])
//                                    )
//                    ) && (
//                            rules['Boss'] ||
//                                    !bossMonsters.hasOwnProperty(monster)
//                    ) && (
//                            !backloggedSources['items'] ||
//                                    !backloggedSources['items'][drop]
//                    )
//            ) {
//                if (!items[drop]) {
//                    items[drop] = {};
//                }
//                if (chunkInfo['drops'][monster][drop][quantity] === 'Always' ||
//                        (
//                                parseInt(secondaryPrimaryNum.split('/')[1]) > 50 &&
//                                        isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', ''))
//                        ) || (
//                        (
//                                chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1 &&
//                                        (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])) < 1
//                        ) ||
//                                (
//                                        !(
//                                                chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1
//                                        ) && (
//                                                parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) /
//                                                        parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')
//                                                        ) >= (
//                                                        parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) /
//                                                                parseFloat(secondaryPrimaryNum.split('/')[1])
//                                                )
//                                        )
//                                )
//                )
//                ) {
//                    items[drop][monster] = 'primary-drop';
//                } else {
//                    items[drop][monster] = 'secondary-drop';
//                }
//                if (!dropRatesGlobal[monster]) {
//                    dropRatesGlobal[monster] = {};
//                }
//                dropRatesGlobal[monster][drop] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//                if (!dropTablesGlobal[monster]) {
//                    dropTablesGlobal[monster] = {};
//                }
//                if (!dropTablesGlobal[monster][drop]) {
//                    dropTablesGlobal[monster][drop] = {};
//                }
//                dropTablesGlobal[monster][drop][quantity] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//            }
//            if (!!dropTables[drop] && ((drop === 'RareDropTable+' || drop === 'GemDropTable+'|| drop === 'GemDropTableLegends+') && rules['RDT'])) {
//                if (!items[drop]) {
//                    items[drop] = {};
//                }
//                if (
//                        chunkInfo['drops'][monster][drop][quantity] === 'Always' ||
//                                (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', ''))) ||
//                                (
//                                        (
//                                                chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1 &&
//                                                        (
//                                                                parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])
//                                                        ) < 1
//                                        ) ||
//                                                (
//                                                        !(chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) &&
//                                                                (
//                                                                        parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) /
//                                                                                parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')) >=
//                                                                                (
//                                                                                        parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) /
//                                                                                                parseFloat(secondaryPrimaryNum.split('/')[1])
//                                                                                )
//                                                                )
//                                                )
//                                )
//                ) {
//                    items[drop][monster] = 'primary-drop';
//                } else {
//                    items[drop][monster] = 'secondary-drop';
//                }
//                if (!dropRatesGlobal[monster]) {
//                    dropRatesGlobal[monster] = {};
//                }
//                dropRatesGlobal[monster][drop] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//                if (!dropTablesGlobal[monster]) {
//                    dropTablesGlobal[monster] = {};
//                }
//                if (!dropTablesGlobal[monster][drop]) {
//                    dropTablesGlobal[monster][drop] = {};
//                }
//                dropTablesGlobal[monster][drop][quantity] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//            }
//                        });
//                    });
//                });
//
//            !!manualMonsters && !!manualMonsters['Monsters'] && Object.keys(manualMonsters['Monsters']).forEach((monster) => {
//                    !rules['Skiller'] && !!chunkInfo['drops'][monster] && (!backloggedSources['monsters'] || !backloggedSources['monsters'][monster]) && Object.keys(chunkInfo['drops'][monster]).forEach((drop) => {
//                            !!chunkInfo['drops'][monster][drop] && Object.keys(chunkInfo['drops'][monster][drop]).forEach((quantity) => {
//            if (!!dropTables[drop] && ((drop !== 'RareDropTable+' && drop !== 'GemDropTable+') || rules['RDT'])) {
//                Object.keys(dropTables[drop]).forEach((item) => {
//                if ((rules['Rare Drop'] || isNaN(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) || ((parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1]) * parseFloat(dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(dropTables[drop][item].split('@')[0].split('/')[1]))) > (parseFloat(rareDropNum.split('/')[0].replaceAll('~', '')) / parseFloat(rareDropNum.split('/')[1]))) &&
//                        (rules['Boss'] || !bossMonsters.hasOwnProperty(monster)) && (!backloggedSources['items'] || !backloggedSources['items'][item])) {
//                    if (!items[item]) {
//                        items[item] = {};
//                    }
//                    if ((chunkInfo['drops'][monster][drop][quantity] === 'Always' && dropTables[drop][item].split('@')[0] === 'Always') || (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && (isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', '')) || isNaN(dropTables[drop][item].split('@')[0].replaceAll('/', '').replaceAll('@', '')))) || (((parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'))) >= (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])))) {
//                        items[item][monster] = 'primary-drop';
//                    } else {
//                        items[item][monster] = 'secondary-drop';
//                    }
//                    if (!dropRatesGlobal[monster]) {
//                        dropRatesGlobal[monster] = {};
//                    }
//                    dropRatesGlobal[monster][item] = findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'));
//                    if (!dropTablesGlobal[monster]) {
//                        dropTablesGlobal[monster] = {};
//                    }
//                    if (!dropTablesGlobal[monster][item]) {
//                        dropTablesGlobal[monster][item] = {};
//                    }
//                    let calcedQuantity;
//                    if (dropTables[drop][item].split('@')[1].includes(' (noted)')) {
//                        if (dropTables[drop][item].split('@')[1].includes(' (F2P)')) {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (noted)')[0] * quantity + ' (noted) (F2P)';
//                        } else {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (noted)')[0] * quantity + ' (noted)';
//                        }
//                    } else {
//                        if (dropTables[drop][item].split('@')[1].includes(' (F2P)')) {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (F2P)')[0] * quantity + ' (F2P)';
//                        } else {
//                            calcedQuantity = dropTables[drop][item].split('@')[1] * quantity;
//                        }
//                    }
//                    dropTablesGlobal[monster][item][calcedQuantity] = findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'));
//                }
//                                });
//            } else if ((rules['Rare Drop'] || isNaN(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) || (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) > (parseFloat(rareDropNum.split('/')[0].replaceAll('~', '')) / parseFloat(rareDropNum.split('/')[1]))) &&
//                    (rules['Boss'] || !bossMonsters.hasOwnProperty(monster)) && (!backloggedSources['items'] || !backloggedSources['items'][drop])) {
//                if (!items[drop]) {
//                    items[drop] = {};
//                }
//                if (chunkInfo['drops'][monster][drop][quantity] === 'Always' || (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', ''))) || ((chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1 && (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])) < 1) || (!(chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) && (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')) >= (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])))))) {
//                    items[drop][monster] = 'primary-drop';
//                } else {
//                    items[drop][monster] = 'secondary-drop';
//                }
//                if (!dropRatesGlobal[monster]) {
//                    dropRatesGlobal[monster] = {};
//                }
//                dropRatesGlobal[monster][drop] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//                if (!dropTablesGlobal[monster]) {
//                    dropTablesGlobal[monster] = {};
//                }
//                if (!dropTablesGlobal[monster][drop]) {
//                    dropTablesGlobal[monster][drop] = {};
//                }
//                dropTablesGlobal[monster][drop][quantity] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//            }
//                        });
//                    });
//                });
//
//            !!chunkInfo['chunks'][num]['Sections'][section] && !!chunkInfo['chunks'][num]['Sections'][section]['Shop'] && Object.keys(chunkInfo['chunks'][num]['Sections'][section]['Shop']).forEach((shop) => {
//                    !!chunkInfo['shopItems'][shop] && (!backloggedSources['shops'] || !backloggedSources['shops'][shop]) && Object.keys(chunkInfo['shopItems'][shop]).forEach((item) => {
//            if ((!minigameShops[shop] || rules['Minigame']) && (!backloggedSources['items'] || !backloggedSources['items'][item])) {
//                if (!items[item]) {
//                    items[item] = {};
//                }
//                items[item][shop] = 'shop';
//            }
//                    });
//                });
//
//            !!chunkInfo['chunks'][num]['Sections'][section] && !!chunkInfo['chunks'][num]['Sections'][section]['Spawn'] && Object.keys(chunkInfo['chunks'][num]['Sections'][section]['Spawn']).forEach((spawn) => {
//            if (!backloggedSources['items'] || !backloggedSources['items'][spawn]) {
//                if (!items[spawn]) {
//                    items[spawn] = {};
//                }
//                items[spawn][num + '-' + section] = rules['Primary Spawns'] ? 'primary-spawn' : 'secondary-spawn';
//            }
//                });
//
//            !!chunkInfo['chunks'][num]['Sections'][section] && !!chunkInfo['chunks'][num]['Sections'][section]['Object'] && Object.keys(chunkInfo['chunks'][num]['Sections'][section]['Object']).forEach((object) => {
//            if (!backloggedSources['objects'] || !backloggedSources['objects'][object]) {
//                if (!objects[object]) {
//                    objects[object] = {};
//                }
//                objects[object][num + '-' + section] = true;
//            }
//                });
//
//            !!chunkInfo['chunks'][num]['Sections'][section] && !!chunkInfo['chunks'][num]['Sections'][section]['Monster'] && Object.keys(chunkInfo['chunks'][num]['Sections'][section]['Monster']).forEach((monster) => {
//            if (!backloggedSources['monsters'] || !backloggedSources['monsters'][monster]) {
//                if (!monsters[monster]) {
//                    monsters[monster] = {};
//                }
//                monsters[monster][num + '-' + section] = true;
//            }
//                });
//
//            !!chunkInfo['chunks'][num]['Sections'][section] && !!chunkInfo['chunks'][num]['Sections'][section]['NPC'] && Object.keys(chunkInfo['chunks'][num]['Sections'][section]['NPC']).forEach((npc) => {
//            if (!backloggedSources['npcs'] || !backloggedSources['npcs'][npc]) {
//                if (!npcs[npc]) {
//                    npcs[npc] = {};
//                }
//                npcs[npc][num + '-' + section] = true;
//            }
//                });
//
//            !!chunkInfo['chunks'][num]['Sections'][section] && !!chunkInfo['chunks'][num]['Sections'][section]['Shop'] && Object.keys(chunkInfo['chunks'][num]['Sections'][section]['Shop']).forEach((shop) => {
//            if (!backloggedSources['shops'] || !backloggedSources['shops'][shop]) {
//                if (!shops[shop]) {
//                    shops[shop] = {};
//                }
//                shops[shop][num + '-' + section] = true;
//            }
//                });
//        }
//        });
                });
            }
//        if (rules['Puro-Puro'] || num !== 'Puro-Puro') {
//            !!chunkInfo['chunks'][num] && !!chunkInfo['chunks'][num]['Monster'] && Object.keys(chunkInfo['chunks'][num]['Monster']).forEach((monster) => {
//                    !rules['Skiller'] && !!chunkInfo['drops'][monster] && (!backloggedSources['monsters'] || !backloggedSources['monsters'][monster]) && Object.keys(chunkInfo['drops'][monster]).forEach((drop) => {
//                            !!chunkInfo['drops'][monster][drop] && Object.keys(chunkInfo['drops'][monster][drop]).forEach((quantity) => {
//            if (!!dropTables[drop] && ((drop !== 'RareDropTable+' && drop !== 'GemDropTable+') || rules['RDT']) && drop !== 'GemDropTableLegends+') {
//                Object.keys(dropTables[drop]).forEach((item) => {
//                if ((drop === 'RareDropTable+' || drop === 'GemDropTable+') && item === 'Chaos talisman') {
//                    (chunkInfo['codeItems']['forceTalisman']['Chaos talisman'].hasOwnProperty(monster) && chunkInfo['codeItems']['forceTalisman']['Chaos talisman'][monster].hasOwnProperty(num))
//                }
//                if ((drop === 'RareDropTable+' || drop === 'GemDropTable+') && ((item === 'Nature talisman' && ((!chunkInfo['chunks'][num].hasOwnProperty('Nickname') && (!chunkInfo['codeItems']['forceTalisman']['Nature talisman'].hasOwnProperty(monster) || !chunkInfo['codeItems']['forceTalisman']['Nature talisman'][monster].hasOwnProperty(num))) || (chunkInfo['codeItems']['forceTalisman']['Chaos talisman'].hasOwnProperty(monster) && chunkInfo['codeItems']['forceTalisman']['Chaos talisman'][monster].hasOwnProperty(num)))) || (item === 'Chaos talisman' && ((chunkInfo['chunks'][num].hasOwnProperty('Nickname') && (!chunkInfo['codeItems']['forceTalisman']['Chaos talisman'].hasOwnProperty(monster) || !chunkInfo['codeItems']['forceTalisman']['Chaos talisman'][monster].hasOwnProperty(num))) || (chunkInfo['codeItems']['forceTalisman']['Nature talisman'].hasOwnProperty(monster) && chunkInfo['codeItems']['forceTalisman']['Nature talisman'][monster].hasOwnProperty(num)))))) {
//                    return;
//                }
//                if ((rules['Rare Drop'] || isNaN(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) || ((parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1]) * parseFloat(dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(dropTables[drop][item].split('@')[0].split('/')[1]))) > (parseFloat(rareDropNum.split('/')[0].replaceAll('~', '')) / parseFloat(rareDropNum.split('/')[1]))) &&
//                        (rules['Boss'] || !bossMonsters.hasOwnProperty(monster)) && (!backloggedSources['items'] || !backloggedSources['items'][item])) {
//                    if (!items[item]) {
//                        items[item] = {};
//                    }
//                    if ((chunkInfo['drops'][monster][drop][quantity] === 'Always' && dropTables[drop][item].split('@')[0] === 'Always') || (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && (isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', '')) || isNaN(dropTables[drop][item].split('@')[0].replaceAll('/', '').replaceAll('@', '')))) || (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')) >= parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1]))) {
//                        items[item][monster] = 'primary-drop';
//                    } else {
//                        items[item][monster] = 'secondary-drop';
//                    }
//                    if (!dropRatesGlobal[monster]) {
//                        dropRatesGlobal[monster] = {};
//                    }
//                    dropRatesGlobal[monster][item] = findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'));
//                    if (!dropTablesGlobal[monster]) {
//                        dropTablesGlobal[monster] = {};
//                    }
//                    if (!dropTablesGlobal[monster][item]) {
//                        dropTablesGlobal[monster][item] = {};
//                    }
//                    let calcedQuantity;
//                    if (dropTables[drop][item].split('@')[1].includes(' (noted)')) {
//                        if (dropTables[drop][item].split('@')[1].includes(' (F2P)')) {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (noted)')[0] * quantity + ' (noted) (F2P)';
//                        } else {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (noted)')[0] * quantity + ' (noted)';
//                        }
//                    } else {
//                        (dropTables[drop][item].split('@')[1].includes('-') ? dropTables[drop][item].split('@')[1] : dropTables[drop][item].split('@')[1] * quantity)
//                        if (dropTables[drop][item].split('@')[1].includes(' (F2P)')) {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (F2P)')[0].includes('-') ? dropTables[drop][item].split('@')[1].split(' (F2P)')[0] : dropTables[drop][item].split('@')[1].split(' (F2P)')[0] * quantity + ' (F2P)';
//                        } else {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].includes('-') ? dropTables[drop][item].split('@')[1] : dropTables[drop][item].split('@')[1] * quantity;
//                        }
//                    }
//                    dropTablesGlobal[monster][item][calcedQuantity] = findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'));
//                }
//                            });
//            } else if ((rules['Rare Drop'] || isNaN(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) || (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) > (parseFloat(rareDropNum.split('/')[0].replaceAll('~', '')) / parseFloat(rareDropNum.split('/')[1]))) &&
//                    (rules['Boss'] || !bossMonsters.hasOwnProperty(monster)) && (!backloggedSources['items'] || !backloggedSources['items'][drop])) {
//                if (!items[drop]) {
//                    items[drop] = {};
//                }
//                if (chunkInfo['drops'][monster][drop][quantity] === 'Always' || (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', ''))) || ((chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1 && (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])) < 1) || (!(chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) && (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')) >= (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])))))) {
//                    items[drop][monster] = 'primary-drop';
//                } else {
//                    items[drop][monster] = 'secondary-drop';
//                }
//                if (!dropRatesGlobal[monster]) {
//                    dropRatesGlobal[monster] = {};
//                }
//                dropRatesGlobal[monster][drop] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//                if (!dropTablesGlobal[monster]) {
//                    dropTablesGlobal[monster] = {};
//                }
//                if (!dropTablesGlobal[monster][drop]) {
//                    dropTablesGlobal[monster][drop] = {};
//                }
//                dropTablesGlobal[monster][drop][quantity] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//            }
//            if (!!dropTables[drop] && ((drop === 'RareDropTable+' || drop === 'GemDropTable+'|| drop === 'GemDropTableLegends+') && rules['RDT'])) {
//                if (!items[drop]) {
//                    items[drop] = {};
//                }
//                if (chunkInfo['drops'][monster][drop][quantity] === 'Always' || (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', ''))) || ((chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1 && (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])) < 1) || (!(chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) && (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')) >= (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])))))) {
//                    items[drop][monster] = 'primary-drop';
//                } else {
//                    items[drop][monster] = 'secondary-drop';
//                }
//                if (!dropRatesGlobal[monster]) {
//                    dropRatesGlobal[monster] = {};
//                }
//                dropRatesGlobal[monster][drop] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//                if (!dropTablesGlobal[monster]) {
//                    dropTablesGlobal[monster] = {};
//                }
//                if (!dropTablesGlobal[monster][drop]) {
//                    dropTablesGlobal[monster][drop] = {};
//                }
//                dropTablesGlobal[monster][drop][quantity] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//            }
//                    });
//                });
//            });
//
//            !!manualMonsters && !!manualMonsters['Monsters'] && Object.keys(manualMonsters['Monsters']).forEach((monster) => {
//                    !rules['Skiller'] && !!chunkInfo['drops'][monster] && (!backloggedSources['monsters'] || !backloggedSources['monsters'][monster]) && Object.keys(chunkInfo['drops'][monster]).forEach((drop) => {
//                            !!chunkInfo['drops'][monster][drop] && Object.keys(chunkInfo['drops'][monster][drop]).forEach((quantity) => {
//            if (!!dropTables[drop] && ((drop !== 'RareDropTable+' && drop !== 'GemDropTable+') || rules['RDT'])) {
//                Object.keys(dropTables[drop]).forEach((item) => {
//                if ((rules['Rare Drop'] || isNaN(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) || ((parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1]) * parseFloat(dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(dropTables[drop][item].split('@')[0].split('/')[1]))) > (parseFloat(rareDropNum.split('/')[0].replaceAll('~', '')) / parseFloat(rareDropNum.split('/')[1]))) &&
//                        (rules['Boss'] || !bossMonsters.hasOwnProperty(monster)) && (!backloggedSources['items'] || !backloggedSources['items'][item])) {
//                    if (!items[item]) {
//                        items[item] = {};
//                    }
//                    if ((chunkInfo['drops'][monster][drop][quantity] === 'Always' && dropTables[drop][item].split('@')[0] === 'Always') || (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && (isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', '')) || isNaN(dropTables[drop][item].split('@')[0].replaceAll('/', '').replaceAll('@', '')))) || (((parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'))) >= (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])))) {
//                        items[item][monster] = 'primary-drop';
//                    } else {
//                        items[item][monster] = 'secondary-drop';
//                    }
//                    if (!dropRatesGlobal[monster]) {
//                        dropRatesGlobal[monster] = {};
//                    }
//                    dropRatesGlobal[monster][item] = findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'));
//                    if (!dropTablesGlobal[monster]) {
//                        dropTablesGlobal[monster] = {};
//                    }
//                    if (!dropTablesGlobal[monster][item]) {
//                        dropTablesGlobal[monster][item] = {};
//                    }
//                    let calcedQuantity;
//                    if (dropTables[drop][item].split('@')[1].includes(' (noted)')) {
//                        if (dropTables[drop][item].split('@')[1].includes(' (F2P)')) {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (noted)')[0] * quantity + ' (noted) (F2P)';
//                        } else {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (noted)')[0] * quantity + ' (noted)';
//                        }
//                    } else {
//                        if (dropTables[drop][item].split('@')[1].includes(' (F2P)')) {
//                            calcedQuantity = dropTables[drop][item].split('@')[1].split(' (F2P)')[0] * quantity + ' (F2P)';
//                        } else {
//                            calcedQuantity = dropTables[drop][item].split('@')[1] * quantity;
//                        }
//                    }
//                    dropTablesGlobal[monster][item][calcedQuantity] = findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '') * dropTables[drop][item].split('@')[0].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1] * dropTables[drop][item].split('@')[0].split('/')[1].replaceAll('~', '')), drop.includes('GeneralSeedDropTable'));
//                }
//                            });
//            } else if ((rules['Rare Drop'] || isNaN(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) || (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1])) > (parseFloat(rareDropNum.split('/')[0].replaceAll('~', '')) / parseFloat(rareDropNum.split('/')[1]))) &&
//                    (rules['Boss'] || !bossMonsters.hasOwnProperty(monster)) && (!backloggedSources['items'] || !backloggedSources['items'][drop])) {
//                if (!items[drop]) {
//                    items[drop] = {};
//                }
//                if (chunkInfo['drops'][monster][drop][quantity] === 'Always' || (parseInt(secondaryPrimaryNum.split('/')[1]) > 50 && isNaN(chunkInfo['drops'][monster][drop][quantity].replaceAll('/', '').replaceAll('@', ''))) || ((chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1 && (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])) < 1) || (!(chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) && (parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')) >= (parseFloat(secondaryPrimaryNum.split('/')[0].replaceAll('~', '')) / parseFloat(secondaryPrimaryNum.split('/')[1])))))) {
//                    items[drop][monster] = 'primary-drop';
//                } else {
//                    items[drop][monster] = 'secondary-drop';
//                }
//                if (!dropRatesGlobal[monster]) {
//                    dropRatesGlobal[monster] = {};
//                }
//                dropRatesGlobal[monster][drop] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//                if (!dropTablesGlobal[monster]) {
//                    dropTablesGlobal[monster] = {};
//                }
//                if (!dropTablesGlobal[monster][drop]) {
//                    dropTablesGlobal[monster][drop] = {};
//                }
//                dropTablesGlobal[monster][drop][quantity] = (chunkInfo['drops'][monster][drop][quantity].split('/').length <= 1) ? chunkInfo['drops'][monster][drop][quantity] : findFraction(parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[0].replaceAll('~', '')) / parseFloat(chunkInfo['drops'][monster][drop][quantity].split('/')[1].replaceAll('~', '')));
//            }
//                    });
//                });
//            });
//
//            !!chunkInfo['chunks'][num] && !!chunkInfo['chunks'][num]['Shop'] && Object.keys(chunkInfo['chunks'][num]['Shop']).forEach((shop) => {
//                    !!chunkInfo['shopItems'][shop] && (!backloggedSources['shops'] || !backloggedSources['shops'][shop]) && Object.keys(chunkInfo['shopItems'][shop]).forEach((item) => {
//            if ((!minigameShops[shop] || rules['Minigame']) && (!backloggedSources['items'] || !backloggedSources['items'][item])) {
//                if (!items[item]) {
//                    items[item] = {};
//                }
//                items[item][shop] = 'shop';
//            }
//                });
//            });
//
//            !!chunkInfo['chunks'][num] && !!chunkInfo['chunks'][num]['Spawn'] && Object.keys(chunkInfo['chunks'][num]['Spawn']).forEach((spawn) => {
//            if (!backloggedSources['items'] || !backloggedSources['items'][spawn]) {
//                if (!items[spawn]) {
//                    items[spawn] = {};
//                }
//                items[spawn][num] = rules['Primary Spawns'] ? 'primary-spawn' : 'secondary-spawn';
//            }
//            });
//
//            !!chunkInfo['chunks'][num] && !!chunkInfo['chunks'][num]['Object'] && Object.keys(chunkInfo['chunks'][num]['Object']).forEach((object) => {
//            if (!backloggedSources['objects'] || !backloggedSources['objects'][object]) {
//                if (!objects[object]) {
//                    objects[object] = {};
//                }
//                objects[object][num] = true;
//            }
//            });
//
//            !!chunkInfo['chunks'][num] && !!chunkInfo['chunks'][num]['Monster'] && Object.keys(chunkInfo['chunks'][num]['Monster']).forEach((monster) => {
//            if (!backloggedSources['monsters'] || !backloggedSources['monsters'][monster]) {
//                if (!monsters[monster]) {
//                    monsters[monster] = {};
//                }
//                monsters[monster][num] = true;
//            }
//            });
//
//            !!chunkInfo['chunks'][num] && !!chunkInfo['chunks'][num]['NPC'] && Object.keys(chunkInfo['chunks'][num]['NPC']).forEach((npc) => {
//            if (!backloggedSources['npcs'] || !backloggedSources['npcs'][npc]) {
//                if (!npcs[npc]) {
//                    npcs[npc] = {};
//                }
//                npcs[npc][num] = true;
//            }
//            });
//
//            !!chunkInfo['chunks'][num] && !!chunkInfo['chunks'][num]['Shop'] && Object.keys(chunkInfo['chunks'][num]['Shop']).forEach((shop) => {
//            if (!backloggedSources['shops'] || !backloggedSources['shops'][shop]) {
//                if (!shops[shop]) {
//                    shops[shop] = {};
//                }
//                shops[shop][num] = true;
//            }
//            });
//        }
        });


        return chunkData;
    }


//        chunksIn.forEach((key, value) -> {
//            chunkInfo.chunks.getOrDefault(key, new Chunk()).Sections.entrySet().stream().filter((section) -> unlockedSections.getOrDefault(key, new HashMap<>()).containsKey(section.getKey())).forEach((entry) -> {
//                Chunk section = entry.getValue();
//                if(rules.get("Puro-Puro") || !Objects.equals(key, "Puro-Puro")) {
//                    section.Monster.forEach((monster, count) -> {
//                        if(!rules.get("Skiller") && chunkInfo.drops.containsKey(monster) && !backloggedSources.getOrDefault("monsters", new HashMap<>()).containsKey(monster)) {
//                            chunkInfo.drops.get(monster).forEach((drop, quantities) -> {
//                                quantities.forEach((quantity, chance) -> {
//                                    Float monsterChance = chunkInfo.getMonsterDropChance(monster, drop, quantity);
//                                    if(dropTables.containsKey(drop) && ((!Objects.equals(drop, "RareDropTable+") && !Objects.equals(drop, "GemDropTable+")) || !rules.get("RDT")) && !Objects.equals(drop, "GemDropTableLegends+")) {
//                                        dropTables.get(drop).forEach((item, probability) -> {
//                                            Float dtChance = chunkInfo.getDropTableChance(drop, item);
//                                            if((Objects.equals(drop, "RareDropTable+") || Objects.equals(drop, "GemDropTable+")) && Objects.equals(item, "Chaos talisman")) {
//                                                return;
//                                            }
//
//                                            if((rules.get("Rare Drop") || (monsterChance == null) || (dtChance == null) || (monsterChance * dtChance) > rareDropNum && (rules.get("Boss") || !bossMonsters.containsKey(monster)) && (!backloggedSources.getOrDefault("items", new HashMap<>()).containsKey(item)))) {
//                                                if(!chunkData.items.containsKey(item)) {
//                                                    chunkData.items.put(item, new HashMap<>());
//                                                }
//
//                                                if((Objects.equals(chance, "Always") && Objects.equals(probability.split("@")[0], "Always")) ||
//                                                    (9999999 > 50 && (monsterChance == null || dtChance == null)) ||
//                                                    (monsterChance * dtChance) >= secondaryPrimaryNum
//                                                ) {
//                                                    chunkData.items.get(item).put(monster, "primary-drop");
//                                                } else {
//                                                    chunkData.items.get(item).put(monster, "secondary-drop");
//                                                }
//
//                                                if(!dropRatesGlobal.containsKey(monster)) {
//                                                    dropRatesGlobal.put(monster, new HashMap<>());
//                                                }
//                                                if(monsterChance != null && dtChance != null) {
//                                                    dropRatesGlobal.get(monster).put(item, findFraction((double) (monsterChance * dtChance), drop.contains("GeneralSeedDropTable")));
//                                                }
//
//                                                if(!dropTablesGlobal.containsKey(monster)) {
//                                                    dropTablesGlobal.put(monster, new HashMap<>());
//                                                }
//                                                if(!dropTablesGlobal.get(monster).containsKey(item)) {
//                                                    dropTablesGlobal.get(monster).put(item, new HashMap<>());
//                                                }
//
//                                                String calcedQuantity;
//                                                if(probability.split("@").length > 1 && probability.split("@")[1].contains(" (noted)")) {
//                                                    if(probability.split("@")[1].contains(" (F2P)")) {
//                                                        calcedQuantity = Integer.parseInt(probability.split("@")[1].split(" (noted)")[0]) * Integer.parseInt(quantity) + " (noted) (F2P)";
//                                                    } else {
//                                                        calcedQuantity = Integer.parseInt(probability.split("@")[1].split(" (noted)")[0]) * Integer.parseInt(quantity) + " (noted)";
//                                                    }
//                                                } else {
//                                                    if(probability.split("@").length > 1 && probability.split("@")[1].contains(" (F2P)")) {
//                                                        calcedQuantity = probability.split("@")[1].split(" (F2P)")[0].contains("-") ? probability.split("@")[1].split(" (F2P)")[0] : Integer.parseInt(probability.split("@")[1].split(" (F2P)")[0]) * Integer.parseInt(quantity) + " (F2P)";
//                                                    } else {
//                                                        calcedQuantity = probability.split("@").length > 1 && probability.split("@")[1].contains("-") ? probability.split("@")[1] : (probability.split("@").length > 1 ? (String.valueOf(Integer.parseInt(probability.split("@")[1]) * Integer.parseInt(quantity))) : "0");
//                                                    }
//                                                }
//                                                if(monsterChance != null && dtChance != null) {
//                                                    dropTablesGlobal.get(monster).get(item).put(calcedQuantity, findFraction((double) (monsterChance * dtChance), drop.contains("GeneralSeedDropTable")));
//                                                }
//                                            }
//                                        });
//                                    } else if((rules.get("Rare Drop") || monsterChance == null || monsterChance > rareDropNum ) && (rules.get("Boss") || !bossMonsters.containsKey(monster)) && !backloggedSources.getOrDefault("items", new HashMap<>()).containsKey(drop)) {
//                                        if(!chunkData.items.containsKey(drop)) {
//                                            chunkData.items.put(drop, new HashMap<>());
//                                        }
//
//                                        if((Objects.equals(chance, "Always") || (9999999 > 50 && monsterChance == null) ||
//                                            ((chance.split("/").length <= 1 && (secondaryPrimaryNum < 1)) || ((monsterChance >= secondaryPrimaryNum)))
//                                        )) {
//                                            chunkData.items.get(drop).put(monster, "primary-drop");
//                                        } else {
//                                            chunkData.items.get(drop).put(monster, "secondary-drop");
//                                        }
//
//                                        if(!dropRatesGlobal.containsKey(monster)) {
//                                            dropRatesGlobal.put(monster, new HashMap<>());
//                                        }
//                                        dropRatesGlobal.get(monster).put(drop, chance.split("/").length <= 1 ? chance : findFraction((double) monsterChance, false));
//
//                                        if(!dropTablesGlobal.containsKey(monster)) {
//                                            dropTablesGlobal.put(monster, new HashMap<>());
//                                        }
//                                        if(!dropTablesGlobal.get(monster).containsKey(drop)) {
//                                            dropTablesGlobal.get(monster).put(drop, new HashMap<>());
//                                        }
//
//                                        dropTablesGlobal.get(monster).get(drop).put(quantity, chance.split("/").length <= 1 ? chance : findFraction((double) monsterChance, false));
//                                    }
//
//                                    if(dropTables.containsKey(drop) && ((Objects.equals(drop, "RareDropTable+") || Objects.equals(drop, "GemDropTable+") || Objects.equals(drop, "GemDropTableLegends+")) && !rules.get("RDT"))) {
//                                        if(!chunkData.items.containsKey(drop)) {
//                                            chunkData.items.put(drop, new HashMap<>());
//                                        }
//
//                                        if((Objects.equals(chance, "Always") || (9999999 > 50 && monsterChance == null) ||
//                                                ((chance.split("/").length <= 1 && (secondaryPrimaryNum < 1)) || ((monsterChance >= secondaryPrimaryNum)))
//                                        )) {
//                                            chunkData.items.get(drop).put(monster, "primary-drop");
//                                        } else {
//                                            chunkData.items.get(drop).put(monster, "secondary-drop");
//                                        }
//
//                                        if(!dropRatesGlobal.containsKey(monster)) {
//                                            dropRatesGlobal.put(monster, new HashMap<>());
//                                        }
//                                        dropRatesGlobal.get(monster).put(drop, chance.split("/").length <= 1 ? chance : findFraction((double) monsterChance, false));
//
//                                        if(!dropTablesGlobal.containsKey(monster)) {
//                                            dropTablesGlobal.put(monster, new HashMap<>());
//                                        }
//                                        if(!dropTablesGlobal.get(monster).containsKey(drop)) {
//                                            dropTablesGlobal.get(monster).put(drop, new HashMap<>());
//                                        }
//
//                                        dropTablesGlobal.get(monster).get(drop).put(quantity, chance.split("/").length <= 1 ? chance : findFraction((double) monsterChance, false));
//                                    }
//                                });
//                            });
//                        }
//                    });
//                }
//            });
//            //TODO: manual monsters
//        });
//        log.info(GSON.toJson(dropTablesGlobal));
//        log.info(GSON.toJson(chunkData));
//
//        return chunkData;
//    }

    private void getAllChunkAreas(Map<String, Boolean> chunksIn) {

        int i = 0;
        Map<String, String> temp = new HashMap<>();
        Map<String, Map<String, Boolean>> temp2 = new HashMap<>();
        Map<String, Boolean> tempChunks = new LinkedHashMap<>(chunksIn);

        while(i < tempChunks.size()) {
            List<String> keys = new ArrayList<>(tempChunks.keySet());
            String key = keys.get(i);
            if(chunkInfo.chunks.containsKey(key)) {
                Chunk chunk = chunkInfo.chunks.get(key);
                chunk.Sections.forEach((sectionId, section) -> {
                    findConnection(chunksIn, temp, temp2, tempChunks, key, section);
                });
                findConnection(chunksIn, temp, temp2, tempChunks, key, chunk);
            }

            i++;
        }

        possibleAreas = temp;
        areasStructure = temp2;

        manualAreas.forEach((key, value) -> {
            if(value && !chunks.containsKey(key)) {
                chunksIn.put(key, true);
            } else if(!value) {
                chunksIn.remove(key);
            }
        });

        chunksIn.forEach((key, value) -> {
            if (value) {
                chunks.put(key, chunkInfo.chunks.get(key));
            }
        });
    }

    private void findConnection(Map<String, Boolean> chunks, Map<String, String> temp, Map<String, Map<String, Boolean>> temp2, Map<String, Boolean> tempChunks, String key, Chunk section) {
        section.Connect.forEach((connectionId, connects) -> {
            if(chunkInfo.chunks.containsKey(connectionId)) {
                Chunk connectedChunk = chunkInfo.chunks.get(connectionId);
                if(!connectedChunk.Name.equals("")) {
                    tempChunks.put(connectedChunk.Name, true);
                    temp.put(connectedChunk.Name, possibleAreas.getOrDefault(connectedChunk.Name, "false"));
                    if(!temp2.containsKey(connectedChunk.Name)) {
                        temp2.put(connectedChunk.Name, new HashMap<>());
                    }
                    Map<String, Boolean> connections = temp2.get(connectedChunk.Name);
                    connections.put(key, true);
                    temp2.put(connectedChunk.Name, connections);
                }
                if(!connectedChunk.Name.equals("") && !tempChunks.containsKey(connectedChunk.Name)) {
                    if(chunkInfo.challenges.Nonskill.getOrDefault(connectedChunk.Name, null) == null) {
                        if(temp2.containsKey(connectedChunk.Name)) {
                            temp2.get(connectedChunk.Name).forEach((subArea, is) -> {
                                if(chunks.containsKey(subArea)) {
                                    chunks.put(connectedChunk.Name, true);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private <T> T loadFromFile(String resourceName, TypeToken<T> tokenType) {
        InputStream stream = ChunkmanCompletionistPanel.class.getResourceAsStream(resourceName);
        Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        return GSON.fromJson(reader, tokenType.getType());
    }
}
