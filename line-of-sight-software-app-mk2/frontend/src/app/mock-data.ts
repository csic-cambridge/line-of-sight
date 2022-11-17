import { Asset } from './types/asset'
import {OrganisationalObjective} from "./types/organisational-objective";
import {FunctionalRequirement} from "./types/functional-requirement";
import {FunctionalOutput} from "./types/functional-output";

export const MOCK_OBJECTIVES: Array<OrganisationalObjective> = [
    {id: '064fecac-d5e8-4ee5-8e96-d4bc63cc95ed', name: 'Objective #1', oirs: ['OIR #1', 'OIR #2'], frs: ['1cfda9b4-1466-4695-8ab2-c19848fa4542']},
    {id: 'fc2aae01-5543-4218-adda-99f459e61295', name: 'Objective #2', oirs: ['OIR #3', 'OIR #4'], frs: ['1cfda9b4-1466-4695-8ab2-c19848fa4542', '884deff4-9a42-4436-8ef2-561777a4f3fd']},
    {id: 'ee183d1b-f9f4-4d7d-8c0a-53f81b198ced', name: 'Objective #3', oirs: [], frs: ['1cfda9b4-1466-4695-8ab2-c19848fa4542']}
];
export const MOCK_FRS: Array<FunctionalRequirement> = [
    {id: '1cfda9b4-1466-4695-8ab2-c19848fa4542', name: 'FR #1', fos: ['0d72e486-74cf-45bc-b11e-cbb7a16b698f', '91990373-dc68-4705-ace8-2a4d36d5bcce', 'fbdd7a8a-57a6-46d1-a2d1-af57943fe5cc']},
    {id: '884deff4-9a42-4436-8ef2-561777a4f3fd', name: 'FR #2', fos: []},
    {id: '2853000a-ec64-4d5b-9dcd-f73d8a79e1cc', name: 'FR #3', fos: ['0d72e486-74cf-45bc-b11e-cbb7a16b698f']},
    {id: '53f26727-63ae-4570-a91a-074f3fbdb110', name: 'FR #4', fos: []}
];
export const MOCK_FOS: Array<FunctionalOutput> = [
    {id: '0d72e486-74cf-45bc-b11e-cbb7a16b698f', data_dictionary_entry: { entry_id: 'EF_35_10_30', text: 'External stairs'}, firs: ['FIR #1', 'FIR #2'], assets: ['5799ee92-f25c-4173-aecd-dfa871719065', '27217551-7641-4e1b-a297-11b49adb91b4']},
    {id: '91990373-dc68-4705-ace8-2a4d36d5bcce', data_dictionary_entry: { entry_id: 'EF_37_17_15', text: 'Chimneys'}, firs: ['FIR #3'], assets: ['5799ee92-f25c-4173-aecd-dfa871719065']},
    {id: 'fbdd7a8a-57a6-46d1-a2d1-af57943fe5cc', data_dictionary_entry: { entry_id: 'EF_70_80', text: 'Lighting'}, firs: [], assets: []}
];
export const MOCK_ASSETS: Array<Asset> = [
    {id: '5799ee92-f25c-4173-aecd-dfa871719065', data_dictionary_entry: { entry_id: 'Ss_15_10_33_34', text: 'Ground gas collection and venting systems'}, airs: ['AIR #1', 'AIR #2', 'AIR #3']},
    {id: '0ee6c05d-c82c-494f-b13c-82f8d64178a1', data_dictionary_entry: { entry_id: 'Ss_15_10_78', text: 'Slurry wall systems'}, airs: ['AIR #4', 'AIR #5', 'AIR #6']},
    {id: '6dcb0cd6-9f63-407c-8d4c-c6d29a5e4a75', data_dictionary_entry: { entry_id: 'Ss_25_13_50_54', text: 'Masonry free-standing wall leaf systems'}, airs: ['AIR #7', 'AIR #8', 'AIR #9']},
    {id: 'b2044e60-b5a4-41dc-9a5d-f7577c91bdc0', data_dictionary_entry: { entry_id: 'Ss_25_14_67_12', text: 'Chain link fencing systems'}, airs: []},
    {id: 'f6148b21-5a2c-427a-ac52-aee3a4181c8f', data_dictionary_entry: { entry_id: 'Ss_25_16_94_95', text: 'Vehicle safety control systems'}, airs: []},
    {id: '27217551-7641-4e1b-a297-11b49adb91b4', data_dictionary_entry: { entry_id: 'Ss_25_20_90_15', text: 'Concrete plain tile cladding systems'}, airs: ['AIR #10']},
    {id: '16b4a26c-dd38-4674-a197-049d7385fa82', data_dictionary_entry: { entry_id: 'Ss_25_38_20_30', text: 'Garage door hardware systems'}, airs: []},
    {id: '4a7272c6-70d1-491e-94c0-6f4bdae29eb5', data_dictionary_entry: { entry_id: 'Ss_30_25_10_10', text: 'Board suspended ceiling systems'}, airs: []},
    {id: '5417ce9a-a329-42a1-90b2-a1badfd13fcb', data_dictionary_entry: { entry_id: 'Ss_32_30_90_12', text: 'Carbon steel weathering systems'}, airs: []},
    {id: 'e6d74d50-9bc7-4aff-98eb-d2480b2d98ae', data_dictionary_entry: { entry_id: 'Ss_40_10_25_25', text: 'Electrical external signage systems'}, airs: []},
    {id: 'ae57eb75-2872-4d5c-a1e8-a385f3de825b', data_dictionary_entry: { entry_id: 'Ss_40_10_30_36', text: 'Hazard signage systems'}, airs: []},
    {id: 'ad4d9d3f-d428-4038-a158-b0538a84632f', data_dictionary_entry: { entry_id: 'Ss_40_10_30_70', text: 'Regulatory signage systems'}, airs: []},
    {id: 'ead84c97-a1a4-4b9a-a286-b2b36cb609ea', data_dictionary_entry: { entry_id: 'Ss_45_75_20_16', text: 'Culvert brush fish pass systems'}, airs: []},
    {id: '68e47638-4bfb-4728-ad9e-e2e4bec22595', data_dictionary_entry: { entry_id: 'Ss_45_75_44', text: 'Lamprey pass systems'}, airs: []},
    {id: '50740e2b-a2b6-491d-a92f-babf0959784a', data_dictionary_entry: { entry_id: 'Ss_50_75_58_07', text: 'Biofiltration systems'}, airs: []},
];

