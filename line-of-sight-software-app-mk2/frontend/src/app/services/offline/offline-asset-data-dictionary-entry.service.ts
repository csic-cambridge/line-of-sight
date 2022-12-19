import { Injectable } from '@angular/core';
import {BaseAssetDictionaryEntryService} from '../base/base-asset-dictionary-entry-service';
import {Observable, of} from 'rxjs';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';

@Injectable({
  providedIn: 'root'
})
export class OfflineAssetDataDictionaryEntryService extends BaseAssetDictionaryEntryService {

    constructor() {
        super();
        this.load('');
    }

    getDataDictionaryEntries(projectId: string): Observable<Array<DataDictionaryEntry>> {
        return of(this.entries$.value);
    }

    load(projectId: string): void {
        const data = [
            {
                entry_id : 'Ss_15',
                text : 'Earthworks, remediation and temporary systems'
            },
            {
                entry_id : 'Ss_15_10',
                text : 'Groundworks and earthworks systems'
            },
            {
                entry_id : 'Ss_15_10_30',
                text : 'Excavating, filling and erosion control systems'
            },
            {
                entry_id : 'Ss_15_10_30_05',
                text : 'Backfill systems'
            },
            {
                entry_id : 'Ss_15_10_30_25',
                text : 'Earthworks excavating systems'
            },
            {
                entry_id : 'Ss_15_10_30_27',
                text : 'Earthworks filling systems'
            },
            {
                entry_id : 'Ss_15_10_30_29',
                text : 'Earthworks filling systems around trees'
            },
            {
                entry_id : 'Ss_15_10_30_31',
                text : 'Earthworks filling systems behind retaining walls'
            },
            {
                entry_id : 'Ss_15_10_30_33',
                text : 'Erosion control systems'
            },
            {
                entry_id : 'Ss_15_10_30_65',
                text : 'Puddled clay lining systems'
            },
            {
                entry_id : 'Ss_15_10_30_90',
                text : 'Topsoil filling systems'
            },
            {
                entry_id : 'Ss_15_10_33',
                text : 'Ground gas disposal systems'
            },
            {
                entry_id : 'Ss_15_10_33_34',
                text : 'Ground gas collection and venting systems'
            },
            {
                entry_id : 'Ss_15_10_35',
                text : 'Ground remediation systems'
            },
            {
                entry_id : 'Ss_15_10_35_07',
                text : 'Bioremediation systems'
            },
            {
                entry_id : 'Ss_15_10_35_23',
                text : 'Deep dry soil mixing systems'
            },
            {
                entry_id : 'Ss_15_10_35_25',
                text : 'Dynamic deep compaction systems'
            },
            {
                entry_id : 'Ss_15_10_35_92',
                text : 'Vibro-compaction systems'
            },
            {
                entry_id : 'Ss_15_10_35_94',
                text : 'Vibro-stone column systems'
            },
            {
                entry_id : 'Ss_15_10_35_97',
                text : 'Wet soil mixing systems'
            },
            {
                entry_id : 'Ss_15_10_45',
                text : 'Landfill systems'
            },
            {
                entry_id : 'Ss_15_10_76',
                text : 'Site waste disposal systems'
            },
            {
                entry_id : 'Ss_15_10_76_21',
                text : 'Dewatering systems'
            },
            {
                entry_id : 'Ss_15_10_78',
                text : 'Slurry wall systems'
            },
            {
                entry_id : 'Ss_15_10_80',
                text : 'Stabilization systems'
            },
            {
                entry_id : 'Ss_15_10_80_15',
                text : 'Concrete column stabilization systems'
            },
            {
                entry_id : 'Ss_15_10_80_70',
                text : 'Rock bolting and dowelling systems'
            },
            {
                entry_id : 'Ss_15_10_80_75',
                text : 'Rock fissure grouting systems'
            },
            {
                entry_id : 'Ss_15_10_80_80',
                text : 'Soil nailing systems'
            },
            {
                entry_id : 'Ss_15_10_80_85',
                text : 'Subterranean void filling systems'
            },
            {
                entry_id : 'Ss_15_30',
                text : 'Remediation, repair and renovation systems'
            },
            {
                entry_id : 'Ss_15_30_10',
                text : 'Building services remediation systems'
            },
            {
                entry_id : 'Ss_15_30_12',
                text : 'Ceramics repair and renovation systems'
            },
            {
                entry_id : 'Ss_15_30_15',
                text : 'Concrete and masonry cleaning systems'
            },
            {
                entry_id : 'Ss_15_30_15_15',
                text : 'Concrete cleaning systems'
            },
            {
                entry_id : 'Ss_15_30_15_50',
                text : 'Masonry cleaning systems'
            },
            {
                entry_id : 'Ss_15_30_15_65',
                text : 'Preliminary concrete cleaning systems'
            },
            {
                entry_id : 'Ss_15_30_15_66',
                text : 'Preliminary masonry cleaning systems'
            },
            {
                entry_id : 'Ss_15_30_17',
                text : 'Concrete repair and renovation systems'
            },
            {
                entry_id : 'Ss_15_30_17_10',
                text : 'Bonded steel plate repair systems'
            },
            {
                entry_id : 'Ss_15_30_17_12',
                text : 'Concrete crack repair or filling systems'
            },
            {
                entry_id : 'Ss_15_30_17_14',
                text : 'Concrete mortar or overlay systems'
            },
            {
                entry_id : 'Ss_15_30_17_16',
                text : 'Concrete protective coating systems'
            },
            {
                entry_id : 'Ss_15_30_17_18',
                text : 'Concrete repair systems'
            },
            {
                entry_id : 'Ss_15_30_17_85',
                text : 'Sprayed concrete repair systems'
            },
            {
                entry_id : 'Ss_15_30_32',
                text : 'Fungus and beetle eradication systems'
            },
            {
                entry_id : 'Ss_15_30_32_05',
                text : 'Beetle eradication systems'
            },
            {
                entry_id : 'Ss_15_30_32_15',
                text : 'Combined fungus and beetle treatment systems'
            },
            {
                entry_id : 'Ss_15_30_32_35',
                text : 'Hot air infestation treatment systems'
            },
            {
                entry_id : 'Ss_15_30_32_50',
                text : 'Masonry fungus treatment systems'
            },
            {
                entry_id : 'Ss_15_30_32_85',
                text : 'Termite treatment systems'
            },
            {
                entry_id : 'Ss_15_30_32_90',
                text : 'Timber fungus treatment systems'
            },
            {
                entry_id : 'Ss_15_30_33',
                text : 'Glass repair and renovation systems'
            },
            {
                entry_id : 'Ss_15_30_50',
                text : 'Masonry repair and renovation systems'
            },
            {
                entry_id : 'Ss_15_30_50_50',
                text : 'Masonry repair systems'
            },
            {
                entry_id : 'Ss_15_30_50_53',
                text : 'Mortar joint repair systems'
            },
            {
                entry_id : 'Ss_15_30_50_55',
                text : 'Mortar repointing systems'
            },
            {
                entry_id : 'Ss_15_30_52',
                text : 'Metal repair and renovation systems'
            },
            {
                entry_id : 'Ss_15_30_52_03',
                text : 'Aluminium repair systems'
            },
            {
                entry_id : 'Ss_15_30_52_15',
                text : 'Copper repair systems'
            },
            {
                entry_id : 'Ss_15_30_52_42',
                text : 'Iron repair systems'
            },
            {
                entry_id : 'Ss_15_30_52_46',
                text : 'Lead repair systems'
            },
            {
                entry_id : 'Ss_15_30_52_84',
                text : 'Steel repair systems'
            },
            {
                entry_id : 'Ss_15_30_65',
                text : 'Pipeline renovation systems'
            },
            {
                entry_id : 'Ss_15_30_65_15',
                text : 'Close-fit plastics pipe lining systems'
            },
            {
                entry_id : 'Ss_15_30_65_17',
                text : 'Continuous plastics pipe lining systems'
            },
            {
                entry_id : 'Ss_15_30_65_20',
                text : 'Cured in place lining systems'
            },
            {
                entry_id : 'Ss_15_30_65_65',
                text : 'Preformed lining systems'
            },
            {
                entry_id : 'Ss_15_30_65_72',
                text : 'Rigidly anchored plastics inner layer (RAPL) lining systems'
            },
            {
                entry_id : 'Ss_15_30_65_80',
                text : 'Spray lining systems'
            },
            {
                entry_id : 'Ss_15_30_65_81',
                text : 'Spirally wound plastics pipe lining systems'
            },
            {
                entry_id : 'Ss_15_30_90',
                text : 'Timber repair and renovation systems'
            },
            {
                entry_id : 'Ss_15_30_90_84',
                text : 'Timber joint strengthening systems'
            },
            {
                entry_id : 'Ss_15_30_90_86',
                text : 'Timber section repair systems'
            },
            {
                entry_id : 'Ss_15_30_90_90',
                text : 'Timber section strengthening systems'
            },
            {
                entry_id : 'Ss_15_95',
                text : 'Temporary works systems'
            },
            {
                entry_id : 'Ss_15_95_15',
                text : 'Temporary preparatory works systems'
            },
            {
                entry_id : 'Ss_15_95_15_10',
                text : 'Borehole systems'
            },
            {
                entry_id : 'Ss_15_95_17',
                text : 'Temporary protection systems'
            },
            {
                entry_id : 'Ss_15_95_17_90',
                text : 'Temporary backfill systems'
            },
            {
                entry_id : 'Ss_15_95_20',
                text : 'Temporary whole entity structural works systems'
            },
            {
                entry_id : 'Ss_15_95_20_30',
                text : 'Flying shores systems'
            },
            {
                entry_id : 'Ss_15_95_20_32',
                text : 'Framed retention structure systems'
            },
            {
                entry_id : 'Ss_15_95_20_70',
                text : 'Raking shores systems'
            },
            {
                entry_id : 'Ss_15_95_20_72',
                text : 'Retention structure systems'
            },
            {
                entry_id : 'Ss_15_95_20_75',
                text : 'Scaffold retention structure systems'
            },
            {
                entry_id : 'Ss_15_95_20_87',
                text : 'Thrust pit systems'
            },
            {
                entry_id : 'Ss_15_95_25',
                text : 'Temporary wall and barrier works systems'
            },
            {
                entry_id : 'Ss_15_95_30',
                text : 'Temporary roof, floor and paving works systems'
            },
            {
                entry_id : 'Ss_15_95_32',
                text : 'Temporary water and land management systems'
            },
            {
                entry_id : 'Ss_15_95_32_85',
                text : 'Sedimentation ponds'
            },
            {
                entry_id : 'Ss_15_95_35',
                text : 'Temporary fixed access, tunnel, shaft, vessel and tower works systems'
            },
            {
                entry_id : 'Ss_15_95_40',
                text : 'Temporary signage and fittings, furnishings and equipment (FF&E) works systems'
            },
            {
                entry_id : 'Ss_15_95_45',
                text : 'Temporary flora and fauna systems'
            },
            {
                entry_id : 'Ss_15_95_45_42',
                text : 'Intercropping systems'
            },
            {
                entry_id : 'Ss_15_95_45_85',
                text : 'Spoil heap temporary crop systems'
            },
            {
                entry_id : 'Ss_15_95_45_90',
                text : 'Topsoil heap temporary crop systems'
            },
            {
                entry_id : 'Ss_15_95_55',
                text : 'Temporary piped supply works systems'
            },
            {
                entry_id : 'Ss_15_95_55_90',
                text : 'Tunnelling compressed air systems'
            },
            {
                entry_id : 'Ss_15_95_60',
                text : 'Temporary heating, cooling and refrigeration works systems'
            },
            {
                entry_id : 'Ss_15_95_65',
                text : 'Temporary ventilation and air conditioning works systems'
            },
            {
                entry_id : 'Ss_15_95_70',
                text : 'Temporary electrical works systems'
            },
            {
                entry_id : 'Ss_15_95_75',
                text : 'Temporary communications, security, safety, control and protection works systems'
            },
            {
                entry_id : 'Ss_15_95_80',
                text : 'Temporary pavement works systems'
            },
            {
                entry_id : 'Ss_15_95_80_02',
                text : 'Temporary access road systems'
            },
            {
                entry_id : 'Ss_15_95_80_72',
                text : 'Temporary roadwork systems'
            },
            {
                entry_id : 'Ss_15_95_85',
                text : 'Temporary process engineering works systems'
            },
            {
                entry_id : 'Ss_15_95_90',
                text : 'Temporary soft facility management works services'
            },
            {
                entry_id : 'Ss_20',
                text : 'Structural systems'
            },
            {
                entry_id : 'Ss_20_05',
                text : 'Substructure systems'
            },
            {
                entry_id : 'Ss_20_05_15',
                text : 'Concrete foundation systems'
            },
            {
                entry_id : 'Ss_20_05_15_65',
                text : 'Precast concrete pad and strip foundation systems'
            },
            {
                entry_id : 'Ss_20_05_15_70',
                text : 'Reinforced concrete pad and strip foundation systems'
            },
            {
                entry_id : 'Ss_20_05_15_71',
                text : 'Reinforced concrete pilecap and ground beam foundation systems'
            },
            {
                entry_id : 'Ss_20_05_15_72',
                text : 'Reinforced concrete raft foundation systems'
            },
            {
                entry_id : 'Ss_20_05_15_80',
                text : 'Steel ground beam foundation systems'
            },
            {
                entry_id : 'Ss_20_05_15_90',
                text : 'Unreinforced concrete foundation systems for cast-in products'
            },
            {
                entry_id : 'Ss_20_05_15_91',
                text : 'Unreinforced concrete pad and strip foundation systems'
            },
            {
                entry_id : 'Ss_20_05_15_92',
                text : 'Unreinforced concrete trench fill foundation systems'
            },
            {
                entry_id : 'Ss_20_05_50',
                text : 'Minor concrete substructure systems'
            },
            {
                entry_id : 'Ss_20_05_50_15',
                text : 'Concrete haunching systems'
            },
            {
                entry_id : 'Ss_20_05_50_65',
                text : 'Precast concrete foundation and plinth systems'
            },
            {
                entry_id : 'Ss_20_05_50_70',
                text : 'Reinforced concrete base or foundation systems'
            },
            {
                entry_id : 'Ss_20_05_50_92',
                text : 'Unreinforced concrete foundation with cast-in products systems'
            },
            {
                entry_id : 'Ss_20_05_50_93',
                text : 'Unreinforced concrete foundation and plinth systems'
            },
            {
                entry_id : 'Ss_20_05_65',
                text : 'Piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_24',
                text : 'Driven precast or prestressed concrete piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_40',
                text : 'In situ concrete augered piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_41',
                text : 'In situ concrete bored piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_42',
                text : 'In situ concrete cased displacement piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_43',
                text : 'In situ concrete displacement piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_44',
                text : 'In situ concrete mini-piling or micro-piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_64',
                text : 'Plunge column piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_76',
                text : 'Screw piling systems'
            },
            {
                entry_id : 'Ss_20_05_65_84',
                text : 'Steel bearing pile systems'
            },
            {
                entry_id : 'Ss_20_05_65_89',
                text : 'Timber piling systems'
            },
            {
                entry_id : 'Ss_20_05_80',
                text : 'Structural grouting systems'
            },
            {
                entry_id : 'Ss_20_05_80_12',
                text : 'Cementitious grout systems'
            },
            {
                entry_id : 'Ss_20_05_80_40',
                text : 'Interstitial grouting systems'
            },
            {
                entry_id : 'Ss_20_05_80_57',
                text : 'Non-cementitious grout systems'
            },
            {
                entry_id : 'Ss_20_05_80_71',
                text : 'Retaining wall cementitious grout systems'
            },
            {
                entry_id : 'Ss_20_05_80_73',
                text : 'Retaining wall self-hardening slurry systems'
            },
            {
                entry_id : 'Ss_20_05_80_80',
                text : 'Soil nailing cementitious grout systems'
            },
            {
                entry_id : 'Ss_20_05_90',
                text : 'Underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_10',
                text : 'Beam and pier unreinforced concrete underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_35',
                text : 'Grouted underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_45',
                text : 'Jacked pile underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_46',
                text : 'Jet grouted underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_50',
                text : 'Mass concrete underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_60',
                text : 'Pile and cantilever support underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_61',
                text : 'Pile and needle underpinning systems'
            },
            {
                entry_id : 'Ss_20_05_90_70',
                text : 'Raking pile underpinning systems'
            },
            {
                entry_id : 'Ss_20_10',
                text : 'Structural frame systems'
            },
            {
                entry_id : 'Ss_20_10_30',
                text : 'Complete bridge structure systems'
            },
            {
                entry_id : 'Ss_20_10_30_30',
                text : 'Footbridge systems'
            },
            {
                entry_id : 'Ss_20_10_30_65',
                text : 'Pipe bridge systems'
            },
            {
                entry_id : 'Ss_20_10_60',
                text : 'Prefabricated framed and panelled structures'
            },
            {
                entry_id : 'Ss_20_10_60_33',
                text : 'Geodesic dome systems'
            },
            {
                entry_id : 'Ss_20_10_60_34',
                text : 'Glazed enclosure systems'
            },
            {
                entry_id : 'Ss_20_10_60_50',
                text : 'Modular pod systems'
            },
            {
                entry_id : 'Ss_20_10_60_60',
                text : 'Panelled and framed modular systems'
            },
            {
                entry_id : 'Ss_20_10_60_84',
                text : 'Structural insulated panel systems'
            },
            {
                entry_id : 'Ss_20_10_60_95',
                text : 'Volumetric modular systems'
            },
            {
                entry_id : 'Ss_20_10_65',
                text : 'Prefabricated room systems'
            },
            {
                entry_id : 'Ss_20_10_65_15',
                text : 'Composite pods'
            },
            {
                entry_id : 'Ss_20_10_65_17',
                text : 'Concrete pods'
            },
            {
                entry_id : 'Ss_20_10_65_65',
                text : 'Polypropylene pods'
            },
            {
                entry_id : 'Ss_20_10_70',
                text : 'Shelter systems'
            },
            {
                entry_id : 'Ss_20_10_70_03',
                text : 'Animal shelter systems'
            },
            {
                entry_id : 'Ss_20_10_70_62',
                text : 'People shelter systems'
            },
            {
                entry_id : 'Ss_20_10_70_64',
                text : 'Plant shelter systems'
            },
            {
                entry_id : 'Ss_20_10_70_84',
                text : 'Storage shelter systems'
            },
            {
                entry_id : 'Ss_20_10_75',
                text : 'Structural framing systems'
            },
            {
                entry_id : 'Ss_20_10_75_35',
                text : 'Heavy steel framing systems'
            },
            {
                entry_id : 'Ss_20_10_75_45',
                text : 'Light steel framing systems'
            },
            {
                entry_id : 'Ss_20_10_75_65',
                text : 'Precast reinforced concrete framing systems'
            },
            {
                entry_id : 'Ss_20_10_75_70',
                text : 'Reinforced concrete framing systems'
            },
            {
                entry_id : 'Ss_20_10_75_85',
                text : 'Timber framing systems'
            },
            {
                entry_id : 'Ss_20_20',
                text : 'Structural beams'
            },
            {
                entry_id : 'Ss_20_20_75',
                text : 'Structural beam systems'
            },
            {
                entry_id : 'Ss_20_20_75_35',
                text : 'Heavy steel beam systems'
            },
            {
                entry_id : 'Ss_20_20_75_45',
                text : 'Light steel beam systems'
            },
            {
                entry_id : 'Ss_20_20_75_65',
                text : 'Precast reinforced concrete beam systems'
            },
            {
                entry_id : 'Ss_20_20_75_70',
                text : 'Reinforced concrete beam systems'
            },
            {
                entry_id : 'Ss_20_20_75_85',
                text : 'Timber beam systems'
            },
            {
                entry_id : 'Ss_20_30',
                text : 'Structural columns'
            },
            {
                entry_id : 'Ss_20_30_75',
                text : 'Structural column systems'
            },
            {
                entry_id : 'Ss_20_30_75_35',
                text : 'Heavy steel column systems'
            },
            {
                entry_id : 'Ss_20_30_75_45',
                text : 'Light steel column systems'
            },
            {
                entry_id : 'Ss_20_30_75_50',
                text : 'Masonry column systems'
            },
            {
                entry_id : 'Ss_20_30_75_65',
                text : 'Precast reinforced concrete column systems'
            },
            {
                entry_id : 'Ss_20_30_75_70',
                text : 'Reinforced concrete column systems'
            },
            {
                entry_id : 'Ss_20_30_75_85',
                text : 'Timber column systems'
            },
            {
                entry_id : 'Ss_20_40',
                text : 'Structural sheet and cable systems'
            },
            {
                entry_id : 'Ss_20_40_10',
                text : 'Building cable systems'
            },
            {
                entry_id : 'Ss_20_50',
                text : 'Bridge structure systems'
            },
            {
                entry_id : 'Ss_20_50_10',
                text : 'Abutment systems'
            },
            {
                entry_id : 'Ss_20_50_10_10',
                text : 'Bank seat abutments'
            },
            {
                entry_id : 'Ss_20_50_10_30',
                text : 'Embedded retaining wall bridge abutments'
            },
            {
                entry_id : 'Ss_20_50_10_40',
                text : 'Integral abutments'
            },
            {
                entry_id : 'Ss_20_50_10_50',
                text : 'Masonry bridge abutments'
            },
            {
                entry_id : 'Ss_20_50_10_70',
                text : 'Reinforced concrete bridge abutments'
            },
            {
                entry_id : 'Ss_20_50_10_80',
                text : 'Sheet piled bridge abutments'
            },
            {
                entry_id : 'Ss_20_50_10_85',
                text : 'Spill-through abutments'
            },
            {
                entry_id : 'Ss_20_50_10_90',
                text : 'Tied embedded retaining wall bridge abutments'
            },
            {
                entry_id : 'Ss_20_50_10_95',
                text : 'Wing wall systems'
            },
            {
                entry_id : 'Ss_20_50_20',
                text : 'Bridge pier systems'
            },
            {
                entry_id : 'Ss_20_50_20_50',
                text : 'Masonry bridge piers'
            },
            {
                entry_id : 'Ss_20_50_20_70',
                text : 'Reinforced concrete bridge piers'
            },
            {
                entry_id : 'Ss_20_50_25',
                text : 'Bridge deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_08',
                text : 'Box girder bridge deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_12',
                text : 'Composite steel and concrete bridge deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_15',
                text : 'Concrete half through deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_16',
                text : 'Concrete slab deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_30',
                text : 'Filler beam deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_31',
                text : 'Flat and stepped deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_53',
                text : 'Modular precast concrete deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_59',
                text : 'Orthotropic deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_65',
                text : 'Prestressed concrete beam deck systems'
            },
            {
                entry_id : 'Ss_20_50_25_70',
                text : 'Reinforced concrete arch bridge systems'
            },
            {
                entry_id : 'Ss_20_50_25_80',
                text : 'Solid steel slab systems'
            },
            {
                entry_id : 'Ss_20_50_30',
                text : 'Abutment and pier component systems'
            },
            {
                entry_id : 'Ss_20_50_30_11',
                text : 'Capping beam systems'
            },
            {
                entry_id : 'Ss_20_50_30_37',
                text : 'Headwall systems'
            },
            {
                entry_id : 'Ss_20_50_30_80',
                text : 'Spandrel arch wall systems'
            },
            {
                entry_id : 'Ss_20_50_35',
                text : 'Bridge cable systems'
            },
            {
                entry_id : 'Ss_20_50_35_11',
                text : 'Cable-stayed bridge cable systems'
            },
            {
                entry_id : 'Ss_20_50_35_85',
                text : 'Suspension bridge cable systems'
            },
            {
                entry_id : 'Ss_20_50_40',
                text : 'Bridge expansion joint systems'
            },
            {
                entry_id : 'Ss_20_50_40_04',
                text : 'Asphaltic plug joint systems'
            },
            {
                entry_id : 'Ss_20_50_40_09',
                text : 'Buried expansion joint systems'
            },
            {
                entry_id : 'Ss_20_50_40_56',
                text : 'Nosing expansion joint systems with poured sealants'
            },
            {
                entry_id : 'Ss_20_50_40_57',
                text : 'Nosing expansion joint systems with preformed compression seals'
            },
            {
                entry_id : 'Ss_20_60',
                text : 'Retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30',
                text : 'Embedded retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_03',
                text : 'Anchored steel sheet pile retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_08',
                text : 'Bored king post retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_13',
                text : 'Combi retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_15',
                text : 'Contiguous pile retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_24',
                text : 'Driven king post retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_70',
                text : 'Reinforced concrete diaphragm retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_80',
                text : 'Secant pile retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_83',
                text : 'Slurry diaphragm retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_85',
                text : 'Steel sheet pile retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_30_92',
                text : 'Unreinforced concrete diaphragm retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35',
                text : 'Gravity retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_10',
                text : 'Caged fill retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_13',
                text : 'Concrete bagwork retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_15',
                text : 'Crib retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_25',
                text : 'Drystack masonry unit retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_50',
                text : 'Masonry retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_53',
                text : 'Mesh formwork and geogrid retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_66',
                text : 'Precast concrete stem wall retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_70',
                text : 'Reinforced concrete retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_72',
                text : 'Reinforced earth retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_90',
                text : 'Timber retaining wall systems'
            },
            {
                entry_id : 'Ss_20_60_35_91',
                text : 'Tyre bale retaining wall systems'
            },
            {
                entry_id : 'Ss_20_70',
                text : 'Structure covering and finishing systems'
            },
            {
                entry_id : 'Ss_20_70_15',
                text : 'Concrete protection systems'
            },
            {
                entry_id : 'Ss_20_70_65',
                text : 'Protective painting systems'
            },
            {
                entry_id : 'Ss_20_70_80',
                text : 'Steel encasement systems'
            },
            {
                entry_id : 'Ss_20_80',
                text : 'Structure accessory systems'
            },
            {
                entry_id : 'Ss_20_80_60',
                text : 'Permanent formwork systems'
            },
            {
                entry_id : 'Ss_20_95',
                text : 'Temporary structural systems'
            },
            {
                entry_id : 'Ss_25',
                text : 'Wall and barrier systems'
            },
            {
                entry_id : 'Ss_25_10',
                text : 'Framed wall systems'
            },
            {
                entry_id : 'Ss_25_10_20',
                text : 'Curtain walling systems'
            },
            {
                entry_id : 'Ss_25_10_20_85',
                text : 'Stick curtain walling systems'
            },
            {
                entry_id : 'Ss_25_10_20_90',
                text : 'Unitized curtain walling systems'
            },
            {
                entry_id : 'Ss_25_10_30',
                text : 'Framed partition systems'
            },
            {
                entry_id : 'Ss_25_10_30_35',
                text : 'Gypsum board partition systems'
            },
            {
                entry_id : 'Ss_25_10_30_70',
                text : 'Rigid sheet partition systems'
            },
            {
                entry_id : 'Ss_25_10_32',
                text : 'Framed wall structure systems'
            },
            {
                entry_id : 'Ss_25_10_32_03',
                text : 'Aluminium wall framing systems'
            },
            {
                entry_id : 'Ss_25_10_32_35',
                text : 'Heavy steel wall framing systems'
            },
            {
                entry_id : 'Ss_25_10_32_45',
                text : 'Light steel wall framing systems'
            },
            {
                entry_id : 'Ss_25_10_32_58',
                text : 'Plastics wall framing systems'
            },
            {
                entry_id : 'Ss_25_10_32_90',
                text : 'Timber wall framing systems'
            },
            {
                entry_id : 'Ss_25_10_35',
                text : 'Framed glazed wall systems'
            },
            {
                entry_id : 'Ss_25_10_35_35',
                text : 'Glazed screen systems'
            },
            {
                entry_id : 'Ss_25_10_35_95',
                text : 'Vertical patent glazing systems'
            },
            {
                entry_id : 'Ss_25_10_35_97',
                text : 'Window wall glazed screen systems'
            },
            {
                entry_id : 'Ss_25_11',
                text : 'Monolithic wall structure systems'
            },
            {
                entry_id : 'Ss_25_11_13',
                text : 'Cob wall systems'
            },
            {
                entry_id : 'Ss_25_11_15',
                text : 'Concrete form masonry unit wall systems'
            },
            {
                entry_id : 'Ss_25_11_16',
                text : 'Concrete wall systems'
            },
            {
                entry_id : 'Ss_25_11_16_62',
                text : 'Post-tensioned concrete wall systems'
            },
            {
                entry_id : 'Ss_25_11_16_65',
                text : 'Precast concrete wall systems'
            },
            {
                entry_id : 'Ss_25_11_16_70',
                text : 'Reinforced concrete wall structure systems'
            },
            {
                entry_id : 'Ss_25_11_16_80',
                text : 'Sprayed concrete wall structure systems'
            },
            {
                entry_id : 'Ss_25_11_70',
                text : 'Rammed earth wall systems'
            },
            {
                entry_id : 'Ss_25_11_70_55',
                text : 'Non-stabilized rammed earth wall systems'
            },
            {
                entry_id : 'Ss_25_11_70_83',
                text : 'Stabilized rammed earth plinth systems'
            },
            {
                entry_id : 'Ss_25_11_70_85',
                text : 'Stabilized rammed earth wall systems'
            },
            {
                entry_id : 'Ss_25_11_90',
                text : 'Timber wall systems'
            },
            {
                entry_id : 'Ss_25_11_90_15',
                text : 'Cross-laminated timber wall systems'
            },
            {
                entry_id : 'Ss_25_11_90_96',
                text : 'Wood log wall systems'
            },
            {
                entry_id : 'Ss_25_12',
                text : 'Panel wall structure systems'
            },
            {
                entry_id : 'Ss_25_12_10',
                text : 'Brick panel wall systems'
            },
            {
                entry_id : 'Ss_25_12_15',
                text : 'Concrete panel wall systems'
            },
            {
                entry_id : 'Ss_25_12_15_05',
                text : 'Autoclaved aerated concrete (AAC) panel wall systems'
            },
            {
                entry_id : 'Ss_25_12_15_15',
                text : 'Composite concrete panel wall systems'
            },
            {
                entry_id : 'Ss_25_12_60',
                text : 'Panel enclosure systems'
            },
            {
                entry_id : 'Ss_25_12_60_30',
                text : 'Fully-framed panel cubicle systems'
            },
            {
                entry_id : 'Ss_25_12_60_60',
                text : 'Panel cubicle systems'
            },
            {
                entry_id : 'Ss_25_12_60_65',
                text : 'Privacy screen systems'
            },
            {
                entry_id : 'Ss_25_12_65',
                text : 'Partitioning systems'
            },
            {
                entry_id : 'Ss_25_12_65_50',
                text : 'Media wall systems'
            },
            {
                entry_id : 'Ss_25_12_65_55',
                text : 'Mesh panel partition systems'
            },
            {
                entry_id : 'Ss_25_12_65_60',
                text : 'Plasterboard laminated partition systems'
            },
            {
                entry_id : 'Ss_25_12_65_65',
                text : 'Panel partition systems'
            },
            {
                entry_id : 'Ss_25_12_65_70',
                text : 'Relocatable partition and ceiling systems'
            },
            {
                entry_id : 'Ss_25_12_65_75',
                text : 'Relocatable partition systems'
            },
            {
                entry_id : 'Ss_25_12_65_85',
                text : 'Storage wall systems'
            },
            {
                entry_id : 'Ss_25_12_70',
                text : 'Retractable partition systems'
            },
            {
                entry_id : 'Ss_25_12_70_30',
                text : 'Fabric dividing partition systems'
            },
            {
                entry_id : 'Ss_25_12_70_80',
                text : 'Sliding stacking panel partition systems'
            },
            {
                entry_id : 'Ss_25_12_80',
                text : 'Structural glass wall systems'
            },
            {
                entry_id : 'Ss_25_12_80_65',
                text : 'Point-fixed structural glass wall systems'
            },
            {
                entry_id : 'Ss_25_12_80_83',
                text : 'Stacked structural glass wall systems'
            },
            {
                entry_id : 'Ss_25_12_80_85',
                text : 'Suspended structural glass wall systems'
            },
            {
                entry_id : 'Ss_25_12_85',
                text : 'Structural steel panel wall systems'
            },
            {
                entry_id : 'Ss_25_12_85_60',
                text : 'Prefabricated metal wall systems'
            },
            {
                entry_id : 'Ss_25_12_85_63',
                text : 'Profile metal sheet wall systems'
            },
            {
                entry_id : 'Ss_25_13',
                text : 'Unit wall structure systems'
            },
            {
                entry_id : 'Ss_25_13_05',
                text : 'Adobe block wall systems'
            },
            {
                entry_id : 'Ss_25_13_24',
                text : 'Dry stone wall systems'
            },
            {
                entry_id : 'Ss_25_13_33',
                text : 'Glass wall systems'
            },
            {
                entry_id : 'Ss_25_13_33_33',
                text : 'Glass block wall systems'
            },
            {
                entry_id : 'Ss_25_13_33_64',
                text : 'Prefabricated glass block wall systems'
            },
            {
                entry_id : 'Ss_25_13_35',
                text : 'Gypsum block wall systems'
            },
            {
                entry_id : 'Ss_25_13_45',
                text : 'Lead brick wall systems'
            },
            {
                entry_id : 'Ss_25_13_50',
                text : 'Masonry wall systems'
            },
            {
                entry_id : 'Ss_25_13_50_49',
                text : 'Masonry exposed feature systems'
            },
            {
                entry_id : 'Ss_25_13_50_51',
                text : 'Masonry wall leaf systems'
            },
            {
                entry_id : 'Ss_25_13_50_54',
                text : 'Masonry free-standing wall leaf systems'
            },
            {
                entry_id : 'Ss_25_13_50_56',
                text : 'Masonry internal partition systems'
            },
            {
                entry_id : 'Ss_25_13_70',
                text : 'Reinforced masonry wall systems'
            },
            {
                entry_id : 'Ss_25_13_70_71',
                text : 'Reinforced masonry wall leaf systems'
            },
            {
                entry_id : 'Ss_25_13_70_74',
                text : 'Reinforced masonry free-standing wall leaf systems'
            },
            {
                entry_id : 'Ss_25_13_70_76',
                text : 'Reinforced masonry internal partition systems'
            },
            {
                entry_id : 'Ss_25_13_85',
                text : 'Straw bale wall systems'
            },
            {
                entry_id : 'Ss_25_13_85_30',
                text : 'Framed straw bale wall systems'
            },
            {
                entry_id : 'Ss_25_13_85_95',
                text : 'Unframed straw bale wall systems'
            },
            {
                entry_id : 'Ss_25_14',
                text : 'Fence systems'
            },
            {
                entry_id : 'Ss_25_14_30',
                text : 'Framed fence systems'
            },
            {
                entry_id : 'Ss_25_14_30_56',
                text : 'Natural stone fencing systems'
            },
            {
                entry_id : 'Ss_25_14_55',
                text : 'Monolithic fence systems'
            },
            {
                entry_id : 'Ss_25_14_63',
                text : 'Post, rail and board fence systems'
            },
            {
                entry_id : 'Ss_25_14_63_06',
                text : 'Bespoke fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_08',
                text : 'Board or palisade fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_11',
                text : 'Carbon steel continuous bar fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_51',
                text : 'Metal post and rail fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_52',
                text : 'Metal vertical pale fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_56',
                text : 'Natural hurdle fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_62',
                text : 'Post and panel fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_63',
                text : 'Post and rail fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_64',
                text : 'Precast concrete palisade fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_65',
                text : 'Precast concrete post and panel fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_66',
                text : 'Precast concrete post and rail fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_67',
                text : 'Prefabricated wood or plastics panel fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_84',
                text : 'Steel hurdle fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_85',
                text : 'Steel palisade panel fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_86',
                text : 'Steel vertical bar fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_95',
                text : 'Wood close boarded fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_96',
                text : 'Wood palisade fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_97',
                text : 'Wood post and rail fencing systems'
            },
            {
                entry_id : 'Ss_25_14_63_98',
                text : 'Wrought iron panel fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67',
                text : 'Post, wire and mesh fence systems'
            },
            {
                entry_id : 'Ss_25_14_67_12',
                text : 'Chain link fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_14',
                text : 'Cleft chestnut pale fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_15',
                text : 'Continuous mesh fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_24',
                text : 'Dropper pattern strained wire fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_27',
                text : 'Electric fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_28',
                text : 'Electric security fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_33',
                text : 'General pattern strained wire fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_34',
                text : 'General pattern wire mesh fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_51',
                text : 'Metal mesh panel fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_70',
                text : 'Rope or chain fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_80',
                text : 'Spring steel and high tensile wire mesh fencing systems'
            },
            {
                entry_id : 'Ss_25_14_67_97',
                text : 'Wire fencing systems'
            },
            {
                entry_id : 'Ss_25_15',
                text : 'Fixed pedestrian barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60',
                text : 'Pedestrian safety barrier and guarding systems'
            },
            {
                entry_id : 'Ss_25_15_60_14',
                text : 'Composite barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_15',
                text : 'Composite pedestrian parapet systems'
            },
            {
                entry_id : 'Ss_25_15_60_16',
                text : 'Composite post and panel barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_17',
                text : 'Composite post and rail barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_18',
                text : 'Concrete post and rail barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_37',
                text : 'Hoop barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_52',
                text : 'Metal modular barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_53',
                text : 'Metal pedestrian parapet systems'
            },
            {
                entry_id : 'Ss_25_15_60_54',
                text : 'Metal post and mesh panel barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_55',
                text : 'Metal post and panel barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_56',
                text : 'Metal post and rail barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_57',
                text : 'Metal vertical bar pedestrian guard rail systems'
            },
            {
                entry_id : 'Ss_25_15_60_60',
                text : 'Pedestrian parapet systems'
            },
            {
                entry_id : 'Ss_25_15_60_65',
                text : 'Post and panel barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_66',
                text : 'Post and rail barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_67',
                text : 'Post and rope barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_68',
                text : 'Post and wire barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_96',
                text : 'Wood post and panel barrier systems'
            },
            {
                entry_id : 'Ss_25_15_60_97',
                text : 'Wood post and rail barrier systems'
            },
            {
                entry_id : 'Ss_25_15_85',
                text : 'Sports barrier systems'
            },
            {
                entry_id : 'Ss_25_15_85_55',
                text : 'Multi-use games fencing systems'
            },
            {
                entry_id : 'Ss_25_16',
                text : 'Fixed traffic and protective barrier systems'
            },
            {
                entry_id : 'Ss_25_16_04',
                text : 'Artificial reef systems'
            },
            {
                entry_id : 'Ss_25_16_05',
                text : 'Avalanche and snow barrier systems'
            },
            {
                entry_id : 'Ss_25_16_08',
                text : 'Blast barrier systems'
            },
            {
                entry_id : 'Ss_25_16_08_44',
                text : 'Jet blast deflection systems'
            },
            {
                entry_id : 'Ss_25_16_21',
                text : 'Debris flow barrier systems'
            },
            {
                entry_id : 'Ss_25_16_46',
                text : 'Landslide barrier systems'
            },
            {
                entry_id : 'Ss_25_16_50',
                text : 'Coastal, river and waterways protection systems'
            },
            {
                entry_id : 'Ss_25_16_50_72',
                text : 'Rock armour systems'
            },
            {
                entry_id : 'Ss_25_16_50_77',
                text : 'Scouring protection systems'
            },
            {
                entry_id : 'Ss_25_16_50_80',
                text : 'Sea wall systems'
            },
            {
                entry_id : 'Ss_25_16_50_82',
                text : 'Splash deck systems'
            },
            {
                entry_id : 'Ss_25_16_50_84',
                text : 'Splash wall systems'
            },
            {
                entry_id : 'Ss_25_16_73',
                text : 'Road and rail barrier systems'
            },
            {
                entry_id : 'Ss_25_16_73_03',
                text : 'Acoustic barrier systems'
            },
            {
                entry_id : 'Ss_25_16_73_05',
                text : 'Anti-throw barrier systems'
            },
            {
                entry_id : 'Ss_25_16_73_26',
                text : 'Environment protection screen systems'
            },
            {
                entry_id : 'Ss_25_16_73_79',
                text : 'Sight barrier systems'
            },
            {
                entry_id : 'Ss_25_16_73_80',
                text : 'Socket for temporary barrier systems'
            },
            {
                entry_id : 'Ss_25_16_76',
                text : 'Rockfall barrier systems'
            },
            {
                entry_id : 'Ss_25_16_94',
                text : 'Vehicle restraint systems'
            },
            {
                entry_id : 'Ss_25_16_94_10',
                text : 'Bollard systems'
            },
            {
                entry_id : 'Ss_25_16_94_14',
                text : 'Combined metal and concrete vehicle safety parapet systems'
            },
            {
                entry_id : 'Ss_25_16_94_16',
                text : 'Concrete safety barrier (CSB) systems'
            },
            {
                entry_id : 'Ss_25_16_94_44',
                text : 'Low-speed precast concrete safety parapet (PCONC) systems'
            },
            {
                entry_id : 'Ss_25_16_94_46',
                text : 'Low-speed masonry safety parapet (PMAS) systems'
            },
            {
                entry_id : 'Ss_25_16_94_48',
                text : 'Low-speed metal vehicle safety parapet systems'
            },
            {
                entry_id : 'Ss_25_16_94_50',
                text : 'Metal vehicle safety fence systems'
            },
            {
                entry_id : 'Ss_25_16_94_65',
                text : 'Protective rail systems'
            },
            {
                entry_id : 'Ss_25_16_94_95',
                text : 'Vehicle safety control systems'
            },
            {
                entry_id : 'Ss_25_16_94_96',
                text : 'Vehicle security barrier systems'
            },
            {
                entry_id : 'Ss_25_16_94_97',
                text : 'Wire rope safety fence (WRSF) systems'
            },
            {
                entry_id : 'Ss_25_17',
                text : 'Dam and levee structure systems'
            },
            {
                entry_id : 'Ss_25_17_05',
                text : 'Arch dam systems'
            },
            {
                entry_id : 'Ss_25_17_10',
                text : 'Buttress dam systems'
            },
            {
                entry_id : 'Ss_25_17_25',
                text : 'Embankment dam and levee systems'
            },
            {
                entry_id : 'Ss_25_17_35',
                text : 'Gravity dam systems'
            },
            {
                entry_id : 'Ss_25_20',
                text : 'Wall cladding systems'
            },
            {
                entry_id : 'Ss_25_20_08',
                text : 'Board cladding systems'
            },
            {
                entry_id : 'Ss_25_20_08_95',
                text : 'Weatherboarding systems'
            },
            {
                entry_id : 'Ss_25_20_14',
                text : 'Composite panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_14_10',
                text : 'Built-up metal insulating sandwich panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_14_52',
                text : 'Metal composite panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_14_54',
                text : 'Metal insulating sandwich panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_14_55',
                text : 'Metal semi-composite panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_15',
                text : 'Concrete cladding systems'
            },
            {
                entry_id : 'Ss_25_20_15_16',
                text : 'Concrete panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_33',
                text : 'Glass fibre-reinforced concrete (GRC) cladding systems'
            },
            {
                entry_id : 'Ss_25_20_33_35',
                text : 'GRC cladding systems'
            },
            {
                entry_id : 'Ss_25_20_34',
                text : 'Glass fibre-reinforced gypsum (GRG) cladding systems'
            },
            {
                entry_id : 'Ss_25_20_34_35',
                text : 'GRG cladding systems'
            },
            {
                entry_id : 'Ss_25_20_35',
                text : 'Glass fibre-reinforced plastics (GRP) cladding systems'
            },
            {
                entry_id : 'Ss_25_20_35_35',
                text : 'GRP cladding systems'
            },
            {
                entry_id : 'Ss_25_20_50',
                text : 'Metal sheet fully supported wall-covering systems'
            },
            {
                entry_id : 'Ss_25_20_50_05',
                text : 'Aluminium sheet fully supported wall-covering systems'
            },
            {
                entry_id : 'Ss_25_20_50_11',
                text : 'Carbon steel sheet fully supported wall-covering systems'
            },
            {
                entry_id : 'Ss_25_20_50_15',
                text : 'Copper sheet fully supported wall-covering systems'
            },
            {
                entry_id : 'Ss_25_20_50_45',
                text : 'Lead sheet fully supported wall-covering systems'
            },
            {
                entry_id : 'Ss_25_20_50_50',
                text : 'Lead-wrapped panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_50_85',
                text : 'Stainless steel sheet fully supported wall-covering systems'
            },
            {
                entry_id : 'Ss_25_20_50_95',
                text : 'Zinc sheet fully supported wall-covering systems'
            },
            {
                entry_id : 'Ss_25_20_60',
                text : 'Panel and sheet cladding systems'
            },
            {
                entry_id : 'Ss_25_20_68',
                text : 'Profiled sheet self-supporting cladding systems'
            },
            {
                entry_id : 'Ss_25_20_68_30',
                text : 'Fibre cement profiled sheet self-supporting cladding systems'
            },
            {
                entry_id : 'Ss_25_20_68_50',
                text : 'Metal profiled sheet self-supporting cladding systems'
            },
            {
                entry_id : 'Ss_25_20_68_65',
                text : 'Plastics profiled sheet self-supporting cladding systems'
            },
            {
                entry_id : 'Ss_25_20_70',
                text : 'Rainscreen cladding systems'
            },
            {
                entry_id : 'Ss_25_20_70_25',
                text : 'Drained and back-ventilated rainscreen cladding systems'
            },
            {
                entry_id : 'Ss_25_20_70_65',
                text : 'Pressure equalized rainscreen cladding systems'
            },
            {
                entry_id : 'Ss_25_20_72',
                text : 'Sheet cladding systems'
            },
            {
                entry_id : 'Ss_25_20_72_72',
                text : 'Rigid sheet cladding systems'
            },
            {
                entry_id : 'Ss_25_20_85',
                text : 'Stone cladding systems'
            },
            {
                entry_id : 'Ss_25_20_85_50',
                text : 'Manufactured stone cladding systems'
            },
            {
                entry_id : 'Ss_25_20_85_55',
                text : 'Natural stone slab cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90',
                text : 'Unit cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90_13',
                text : 'Clay plain tile cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90_15',
                text : 'Concrete plain tile cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90_30',
                text : 'Fibre cement slate cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90_50',
                text : 'Metal tile cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90_55',
                text : 'Natural slate cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90_65',
                text : 'Precast concrete panel cladding systems'
            },
            {
                entry_id : 'Ss_25_20_90_95',
                text : 'Wood shingle or shake cladding systems'
            },
            {
                entry_id : 'Ss_25_25',
                text : 'Wall lining systems'
            },
            {
                entry_id : 'Ss_25_25_05',
                text : 'Acoustic panel systems'
            },
            {
                entry_id : 'Ss_25_25_45',
                text : 'Lining and casing systems'
            },
            {
                entry_id : 'Ss_25_25_45_25',
                text : 'Duct and wall panel lining systems'
            },
            {
                entry_id : 'Ss_25_25_45_33',
                text : 'Gypsum board casing systems'
            },
            {
                entry_id : 'Ss_25_25_45_35',
                text : 'Gypsum board wall lining systems'
            },
            {
                entry_id : 'Ss_25_25_45_47',
                text : 'Linear wall lining systems'
            },
            {
                entry_id : 'Ss_25_25_45_50',
                text : 'Metal framed wall lining systems'
            },
            {
                entry_id : 'Ss_25_25_45_68',
                text : 'Rigid sheet casing systems'
            },
            {
                entry_id : 'Ss_25_25_45_70',
                text : 'Rigid sheet wall lining systems'
            },
            {
                entry_id : 'Ss_25_25_45_88',
                text : 'Timber board casing systems'
            },
            {
                entry_id : 'Ss_25_25_45_90',
                text : 'Timber board wall lining systems'
            },
            {
                entry_id : 'Ss_25_25_70',
                text : 'Radiation shielding lining systems'
            },
            {
                entry_id : 'Ss_25_25_75',
                text : 'Rigid sheet fine lining and panelling systems'
            },
            {
                entry_id : 'Ss_25_25_75_05',
                text : 'Acoustic panel lining systems'
            },
            {
                entry_id : 'Ss_25_25_75_63',
                text : 'Plastics panel lining systems'
            },
            {
                entry_id : 'Ss_25_25_75_65',
                text : 'Plastics veneered panel lining systems'
            },
            {
                entry_id : 'Ss_25_25_75_85',
                text : 'Specialist faced panel lining systems'
            },
            {
                entry_id : 'Ss_25_25_75_94',
                text : 'Wood panel lining systems'
            },
            {
                entry_id : 'Ss_25_25_75_95',
                text : 'Wood veneered panel lining systems'
            },
            {
                entry_id : 'Ss_25_25_85',
                text : 'Stone lining systems'
            },
            {
                entry_id : 'Ss_25_25_85_50',
                text : 'Manufactured stone lining systems'
            },
            {
                entry_id : 'Ss_25_25_85_55',
                text : 'Natural stone lining systems'
            },
            {
                entry_id : 'Ss_25_25_95',
                text : 'Wall sheathing systems'
            },
            {
                entry_id : 'Ss_25_25_95_28',
                text : 'External wall sheathing systems'
            },
            {
                entry_id : 'Ss_25_30',
                text : 'Door and window systems'
            },
            {
                entry_id : 'Ss_25_30_15',
                text : 'Concrete wall glazing systems'
            },
            {
                entry_id : 'Ss_25_30_15_66',
                text : 'Precast concrete security window systems'
            },
            {
                entry_id : 'Ss_25_30_20',
                text : 'Door, shutter and hatch systems'
            },
            {
                entry_id : 'Ss_25_30_20_16',
                text : 'Collapsible gate and grille doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_22',
                text : 'Door assembly systems'
            },
            {
                entry_id : 'Ss_25_30_20_25',
                text : 'Doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_30',
                text : 'Frame and door leaf systems'
            },
            {
                entry_id : 'Ss_25_30_20_32',
                text : 'Frameless glass door systems'
            },
            {
                entry_id : 'Ss_25_30_20_35',
                text : 'Hatch systems'
            },
            {
                entry_id : 'Ss_25_30_20_37',
                text : 'High-security doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_38',
                text : 'High-speed doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_39',
                text : 'Hinged doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_40',
                text : 'Industrial doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_45',
                text : 'Loading bay doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_46',
                text : 'Louvre doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_62',
                text : 'Pivot doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_65',
                text : 'Platform door systems'
            },
            {
                entry_id : 'Ss_25_30_20_70',
                text : 'Revolving doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_73',
                text : 'Roller grille doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_74',
                text : 'Roller shutter doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_76',
                text : 'Sectional overhead doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_77',
                text : 'Sliding doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_78',
                text : 'Sliding folding doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_84',
                text : 'Strip curtain doorset systems'
            },
            {
                entry_id : 'Ss_25_30_20_93',
                text : 'Up-and-over doorset systems'
            },
            {
                entry_id : 'Ss_25_30_29',
                text : 'Fire and smoke curtain systems'
            },
            {
                entry_id : 'Ss_25_30_29_30',
                text : 'Fire curtain systems'
            },
            {
                entry_id : 'Ss_25_30_29_80',
                text : 'Smoke curtain systems'
            },
            {
                entry_id : 'Ss_25_30_95',
                text : 'Window and window walling systems'
            },
            {
                entry_id : 'Ss_25_30_95_95',
                text : 'Window systems'
            },
            {
                entry_id : 'Ss_25_30_95_96',
                text : 'Window walling systems'
            },
            {
                entry_id : 'Ss_25_32',
                text : 'Gate and access control systems'
            },
            {
                entry_id : 'Ss_25_32_35',
                text : 'Gate systems'
            },
            {
                entry_id : 'Ss_25_32_35_05',
                text : 'Animal gate systems'
            },
            {
                entry_id : 'Ss_25_32_35_15',
                text : 'Chicane systems'
            },
            {
                entry_id : 'Ss_25_32_35_30',
                text : 'Folding gate systems'
            },
            {
                entry_id : 'Ss_25_32_35_37',
                text : 'Hinged gate systems'
            },
            {
                entry_id : 'Ss_25_32_35_41',
                text : 'In-line inhibitor systems'
            },
            {
                entry_id : 'Ss_25_32_35_45',
                text : 'Kissing gate systems'
            },
            {
                entry_id : 'Ss_25_32_35_80',
                text : 'Sliding gate systems'
            },
            {
                entry_id : 'Ss_25_32_35_85',
                text : 'Stile systems'
            },
            {
                entry_id : 'Ss_25_32_35_90',
                text : 'Turnstile systems'
            },
            {
                entry_id : 'Ss_25_32_60',
                text : 'Access control gate systems'
            },
            {
                entry_id : 'Ss_25_32_60_15',
                text : 'Controlled pedestrian access gate systems'
            },
            {
                entry_id : 'Ss_25_32_60_26',
                text : 'e-passport gate systems'
            },
            {
                entry_id : 'Ss_25_32_60_60',
                text : 'Passport gate systems'
            },
            {
                entry_id : 'Ss_25_32_70',
                text : 'Railway platform automated barrier systems'
            },
            {
                entry_id : 'Ss_25_36',
                text : 'Operable barrier systems'
            },
            {
                entry_id : 'Ss_25_36_24',
                text : 'Dry dock automated gate systems'
            },
            {
                entry_id : 'Ss_25_36_30',
                text : 'Flood and storm surge barrier systems'
            },
            {
                entry_id : 'Ss_25_36_30_30',
                text : 'Flood gate systems'
            },
            {
                entry_id : 'Ss_25_36_50',
                text : 'Marina and sea gate automated systems'
            },
            {
                entry_id : 'Ss_25_36_58',
                text : 'Operable rail barrier systems'
            },
            {
                entry_id : 'Ss_25_36_62',
                text : 'Operable water barrier systems'
            },
            {
                entry_id : 'Ss_25_36_62_47',
                text : 'Lock gate systems'
            },
            {
                entry_id : 'Ss_25_36_62_96',
                text : 'Water control gate systems'
            },
            {
                entry_id : 'Ss_25_36_64',
                text : 'Operable bridge barrier systems'
            },
            {
                entry_id : 'Ss_25_36_66',
                text : 'Operable tunnel barrier systems'
            },
            {
                entry_id : 'Ss_25_36_70',
                text : 'Railway crossing systems'
            },
            {
                entry_id : 'Ss_25_36_95',
                text : 'Vehicle access point control systems'
            },
            {
                entry_id : 'Ss_25_36_95_05',
                text : 'Automated heavy-duty gate systems'
            },
            {
                entry_id : 'Ss_25_36_95_35',
                text : 'Heavy-duty sliding beam barrier systems'
            },
            {
                entry_id : 'Ss_25_36_95_36',
                text : 'Height restrictor systems'
            },
            {
                entry_id : 'Ss_25_36_95_38',
                text : 'Horizontal swing gate systems'
            },
            {
                entry_id : 'Ss_25_36_95_40',
                text : 'Hydraulic ramp systems'
            },
            {
                entry_id : 'Ss_25_36_95_50',
                text : 'Movable bollard and car park post systems'
            },
            {
                entry_id : 'Ss_25_36_95_70',
                text : 'Rising arm barrier systems'
            },
            {
                entry_id : 'Ss_25_36_95_72',
                text : 'Rising bollard systems'
            },
            {
                entry_id : 'Ss_25_36_95_85',
                text : 'Static bollard systems'
            },
            {
                entry_id : 'Ss_25_36_95_94',
                text : 'Vehicle flow plate systems'
            },
            {
                entry_id : 'Ss_25_36_95_96',
                text : 'Vehicle stop systems'
            },
            {
                entry_id : 'Ss_25_38',
                text : 'Wall and barrier opening hardware systems'
            },
            {
                entry_id : 'Ss_25_38_10',
                text : 'Barrier and gate control systems'
            },
            {
                entry_id : 'Ss_25_38_10_33',
                text : 'Gate hardware systems'
            },
            {
                entry_id : 'Ss_25_38_10_95',
                text : 'Water control gate displacer systems'
            },
            {
                entry_id : 'Ss_25_38_20',
                text : 'Door and window hardware systems'
            },
            {
                entry_id : 'Ss_25_38_20_20',
                text : 'Door hardware systems'
            },
            {
                entry_id : 'Ss_25_38_20_30',
                text : 'Garage door hardware systems'
            },
            {
                entry_id : 'Ss_25_38_20_35',
                text : 'Hatch hardware systems'
            },
            {
                entry_id : 'Ss_25_38_20_70',
                text : 'Relocatable partition hardware systems'
            },
            {
                entry_id : 'Ss_25_38_20_80',
                text : 'Shutter hardware systems'
            },
            {
                entry_id : 'Ss_25_38_20_95',
                text : 'Window hardware systems'
            },
            {
                entry_id : 'Ss_25_45',
                text : 'Wall covering and finish systems'
            },
            {
                entry_id : 'Ss_25_45_02',
                text : 'Acoustic covering systems'
            },
            {
                entry_id : 'Ss_25_45_12',
                text : 'Cavity wall insulation systems'
            },
            {
                entry_id : 'Ss_25_45_12_40',
                text : 'Injected and blown cavity wall insulation systems'
            },
            {
                entry_id : 'Ss_25_45_25',
                text : 'Drapery systems'
            },
            {
                entry_id : 'Ss_25_45_70',
                text : 'Render and roughcast coating systems'
            },
            {
                entry_id : 'Ss_25_45_70_55',
                text : 'Multicoat render systems'
            },
            {
                entry_id : 'Ss_25_45_70_60',
                text : 'One-coat render systems'
            },
            {
                entry_id : 'Ss_25_45_70_65',
                text : 'Parge coat systems'
            },
            {
                entry_id : 'Ss_25_45_70_90',
                text : 'Tiling and mosaic render substrate systems'
            },
            {
                entry_id : 'Ss_25_45_72',
                text : 'Wall insulation systems'
            },
            {
                entry_id : 'Ss_25_45_72_02',
                text : 'Adhesive-fixed external wall insulation systems'
            },
            {
                entry_id : 'Ss_25_45_72_28',
                text : 'External wall insulation systems'
            },
            {
                entry_id : 'Ss_25_45_72_42',
                text : 'Insulated plinth systems'
            },
            {
                entry_id : 'Ss_25_45_72_70',
                text : 'Rail-fixed external wall insulation systems'
            },
            {
                entry_id : 'Ss_25_45_74',
                text : 'Rolled paper and fabric covering systems'
            },
            {
                entry_id : 'Ss_25_45_74_70',
                text : 'Rolled covering systems'
            },
            {
                entry_id : 'Ss_25_45_85',
                text : 'Sprayed coating systems'
            },
            {
                entry_id : 'Ss_25_45_85_82',
                text : 'Sprayed external monolithic coating systems'
            },
            {
                entry_id : 'Ss_25_45_85_85',
                text : 'Sprayed internal monolithic coating systems'
            },
            {
                entry_id : 'Ss_25_45_86',
                text : 'Suspended render systems'
            },
            {
                entry_id : 'Ss_25_45_88',
                text : 'Tiling systems'
            },
            {
                entry_id : 'Ss_25_45_88_25',
                text : 'External wall tiling systems'
            },
            {
                entry_id : 'Ss_25_45_88_40',
                text : 'Internal wall tiling systems'
            },
            {
                entry_id : 'Ss_25_45_88_88',
                text : 'Terrazzo wall tiling systems'
            },
            {
                entry_id : 'Ss_25_45_88_90',
                text : 'Tiling and mosaic mortar substrate systems'
            },
            {
                entry_id : 'Ss_25_45_90',
                text : 'Unit facing systems'
            },
            {
                entry_id : 'Ss_25_50',
                text : 'Wall-mounted canopy and screen systems'
            },
            {
                entry_id : 'Ss_25_50_05',
                text : 'Awning systems'
            },
            {
                entry_id : 'Ss_25_50_35',
                text : 'Grille systems'
            },
            {
                entry_id : 'Ss_25_50_45',
                text : 'Louvre and shading systems'
            },
            {
                entry_id : 'Ss_25_50_45_10',
                text : 'Canopy systems'
            },
            {
                entry_id : 'Ss_25_50_45_30',
                text : 'Facade-aligned brise soleil systems'
            },
            {
                entry_id : 'Ss_25_50_45_45',
                text : 'Louvre screen systems'
            },
            {
                entry_id : 'Ss_25_50_45_60',
                text : 'Panellized louvre screen systems'
            },
            {
                entry_id : 'Ss_25_50_45_65',
                text : 'Projecting brise soleil systems'
            },
            {
                entry_id : 'Ss_25_50_75',
                text : 'Screen systems'
            },
            {
                entry_id : 'Ss_25_50_80',
                text : 'Shutter systems'
            },
            {
                entry_id : 'Ss_25_60',
                text : 'Wall and barrier accessory systems'
            },
            {
                entry_id : 'Ss_25_60_05',
                text : 'Balustrade and handrail systems'
            },
            {
                entry_id : 'Ss_25_60_05_05',
                text : 'Balustrade and guarding systems'
            },
            {
                entry_id : 'Ss_25_60_05_35',
                text : 'Handrail systems'
            },
            {
                entry_id : 'Ss_25_60_10',
                text : 'Cavity wall tie renewal and insertion systems'
            },
            {
                entry_id : 'Ss_25_60_30',
                text : 'Fire-stopping systems'
            },
            {
                entry_id : 'Ss_25_60_30_40',
                text : 'Individual services penetrations fire-stopping systems'
            },
            {
                entry_id : 'Ss_25_60_30_45',
                text : 'Linear gap fire-stopping systems'
            },
            {
                entry_id : 'Ss_25_60_30_48',
                text : 'Loose fire-stopping systems'
            },
            {
                entry_id : 'Ss_25_60_30_55',
                text : 'Multiple services penetrations fire-stopping systems'
            },
            {
                entry_id : 'Ss_25_60_30_62',
                text : 'Pipe collar fire-stopping systems'
            },
            {
                entry_id : 'Ss_25_60_35',
                text : 'Glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_06',
                text : 'Bead-fixed insulating glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_08',
                text : 'Bead-fixed single glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_30',
                text : 'Fire-resistant glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_33',
                text : 'Glazing film systems'
            },
            {
                entry_id : 'Ss_25_60_35_35',
                text : 'Groove-fixed single glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_40',
                text : 'Internal use bead and tape, groove or channel glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_50',
                text : 'Mirror glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_65',
                text : 'Putty-fronted single glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_75',
                text : 'Single-sided gasket glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_85',
                text : 'Structural zipper gasket glazing systems'
            },
            {
                entry_id : 'Ss_25_60_35_95',
                text : 'U-profiled glazing systems'
            },
            {
                entry_id : 'Ss_25_95',
                text : 'Temporary wall and barrier systems'
            },
            {
                entry_id : 'Ss_25_95_30',
                text : 'Temporary flood barrier systems'
            },
            {
                entry_id : 'Ss_25_95_60',
                text : 'Temporary pedestrian barrier systems'
            },
            {
                entry_id : 'Ss_25_95_70',
                text : 'Temporary rail barrier systems'
            },
            {
                entry_id : 'Ss_25_95_75',
                text : 'Temporary road barrier systems'
            },
            {
                entry_id : 'Ss_25_95_85',
                text : 'Temporary traffic barrier systems'
            },
            {
                entry_id : 'Ss_25_95_90',
                text : 'Temporary water barrier systems'
            },
            {
                entry_id : 'Ss_30',
                text : 'Roof, floor and paving systems'
            },
            {
                entry_id : 'Ss_30_10',
                text : 'Roof structure systems'
            },
            {
                entry_id : 'Ss_30_10_30',
                text : 'Framed roof structure systems'
            },
            {
                entry_id : 'Ss_30_10_30_03',
                text : 'Aluminium roof framing systems'
            },
            {
                entry_id : 'Ss_30_10_30_20',
                text : 'Glazed unit roof framing systems'
            },
            {
                entry_id : 'Ss_30_10_30_25',
                text : 'Heavy steel roof framing systems'
            },
            {
                entry_id : 'Ss_30_10_30_30',
                text : 'Heavy steel roof space frame systems'
            },
            {
                entry_id : 'Ss_30_10_30_35',
                text : 'Heavy steel roof space truss systems'
            },
            {
                entry_id : 'Ss_30_10_30_45',
                text : 'Light steel roof framing systems'
            },
            {
                entry_id : 'Ss_30_10_30_58',
                text : 'Plastics roof framing systems'
            },
            {
                entry_id : 'Ss_30_10_30_60',
                text : 'Precast concrete roof structure systems'
            },
            {
                entry_id : 'Ss_30_10_30_70',
                text : 'Reinforced concrete roof framing systems'
            },
            {
                entry_id : 'Ss_30_10_30_85',
                text : 'Timber roof framing systems'
            },
            {
                entry_id : 'Ss_30_10_50',
                text : 'Monolithic roof structure systems'
            },
            {
                entry_id : 'Ss_30_10_50_70',
                text : 'Sprayed concrete roof systems'
            },
            {
                entry_id : 'Ss_30_10_60',
                text : 'Air-pressurized panel roof structure systems'
            },
            {
                entry_id : 'Ss_30_10_60_28',
                text : 'Ethylene tetraflourethylene (ETFE) roof systems'
            },
            {
                entry_id : 'Ss_30_10_90',
                text : 'Unit roof structure systems'
            },
            {
                entry_id : 'Ss_30_10_90_50',
                text : 'Masonry roof systems'
            },
            {
                entry_id : 'Ss_30_12',
                text : 'Floor and deck structure systems'
            },
            {
                entry_id : 'Ss_30_12_05',
                text : 'Beam and block floor systems'
            },
            {
                entry_id : 'Ss_30_12_15',
                text : 'Concrete plank floor systems'
            },
            {
                entry_id : 'Ss_30_12_20',
                text : 'Deck systems'
            },
            {
                entry_id : 'Ss_30_12_32',
                text : 'Framed decking systems'
            },
            {
                entry_id : 'Ss_30_12_33',
                text : 'Framed floor systems'
            },
            {
                entry_id : 'Ss_30_12_33_50',
                text : 'Light steel-framed floor systems'
            },
            {
                entry_id : 'Ss_30_12_33_90',
                text : 'Timber-framed floor systems'
            },
            {
                entry_id : 'Ss_30_12_35',
                text : 'Glass unit masonry floor systems'
            },
            {
                entry_id : 'Ss_30_12_45',
                text : 'Lift-up concrete plank floor systems'
            },
            {
                entry_id : 'Ss_30_12_50',
                text : 'Masonry (flat arch) floor systems'
            },
            {
                entry_id : 'Ss_30_12_60',
                text : 'Pier and jetty structure systems'
            },
            {
                entry_id : 'Ss_30_12_64',
                text : 'Platform systems'
            },
            {
                entry_id : 'Ss_30_12_64_17',
                text : 'Crosswall platform systems'
            },
            {
                entry_id : 'Ss_30_12_64_30',
                text : 'Front wall platform systems'
            },
            {
                entry_id : 'Ss_30_12_64_53',
                text : 'Modular platform systems'
            },
            {
                entry_id : 'Ss_30_12_85',
                text : 'Structural deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_15',
                text : 'Composite concrete deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_16',
                text : 'Composite steel and concrete deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_17',
                text : 'Concrete beam and block deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_18',
                text : 'Concrete deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_30',
                text : 'Glazed unit deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_40',
                text : 'Heavy steel deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_50',
                text : 'Light steel deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_65',
                text : 'Post-tensioned concrete deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_70',
                text : 'Reinforced concrete deck systems'
            },
            {
                entry_id : 'Ss_30_12_85_90',
                text : 'Timber deck systems'
            },
            {
                entry_id : 'Ss_30_14',
                text : 'Paving systems'
            },
            {
                entry_id : 'Ss_30_14_02',
                text : 'Acrylic and resin bound aggregate paving systems'
            },
            {
                entry_id : 'Ss_30_14_02_30',
                text : 'Fibre-reinforced resin-bound aggregate overlay systems'
            },
            {
                entry_id : 'Ss_30_14_02_41',
                text : 'Impact absorbing synthetic play and sport paving systems'
            },
            {
                entry_id : 'Ss_30_14_02_42',
                text : 'Impervious acrylic coated sports paving systems'
            },
            {
                entry_id : 'Ss_30_14_02_70',
                text : 'Resin-bonded aggregate paving systems'
            },
            {
                entry_id : 'Ss_30_14_02_71',
                text : 'Resin-bound aggregate light-duty paving systems'
            },
            {
                entry_id : 'Ss_30_14_02_72',
                text : 'Resin-bound aggregate heavy-duty paving systems'
            },
            {
                entry_id : 'Ss_30_14_02_73',
                text : 'Resin-bound aggregate overlay systems'
            },
            {
                entry_id : 'Ss_30_14_02_74',
                text : 'Resin-bound stone aggregate sports paving systems'
            },
            {
                entry_id : 'Ss_30_14_02_85',
                text : 'Synthetic play surfacing systems'
            },
            {
                entry_id : 'Ss_30_14_05',
                text : 'Asphalt road and paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_05',
                text : 'Asphalt concrete light-duty paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_06',
                text : 'Asphalt concrete paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_07',
                text : 'Asphalt concrete runway paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_08',
                text : 'Asphalt concrete surface overlay systems'
            },
            {
                entry_id : 'Ss_30_14_05_35',
                text : 'Hot rolled asphalt paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_36',
                text : 'Hot rolled asphalt runway paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_40',
                text : 'Hot rolled asphalt sports paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_50',
                text : 'Mastic asphalt pedestrian paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_65',
                text : 'Porous asphalt concrete paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_70',
                text : 'Porous asphalt concrete sports paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_75',
                text : 'Stone mastic asphalt paving systems'
            },
            {
                entry_id : 'Ss_30_14_05_80',
                text : 'Slurry surfacing overlay systems'
            },
            {
                entry_id : 'Ss_30_14_15',
                text : 'Concrete road and paving systems'
            },
            {
                entry_id : 'Ss_30_14_15_14',
                text : 'Concrete grass-filled paving systems'
            },
            {
                entry_id : 'Ss_30_14_15_16',
                text : 'Concrete paving systems'
            },
            {
                entry_id : 'Ss_30_14_15_17',
                text : 'Concrete porous paving systems'
            },
            {
                entry_id : 'Ss_30_14_15_18',
                text : 'Concrete runway paving systems'
            },
            {
                entry_id : 'Ss_30_14_50',
                text : 'Mat and sheet paving systems'
            },
            {
                entry_id : 'Ss_30_14_50_25',
                text : 'Dry ski slope synthetic matting systems'
            },
            {
                entry_id : 'Ss_30_14_50_65',
                text : 'Polymeric sports paving systems'
            },
            {
                entry_id : 'Ss_30_14_50_80',
                text : 'Synthetic grass surfacing systems'
            },
            {
                entry_id : 'Ss_30_14_80',
                text : 'Unbound aggregate paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_30',
                text : 'Fast dry clay sports paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_37',
                text : 'Hoggin paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_40',
                text : 'Hard binding gravel paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_45',
                text : 'Loose laid aggregate paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_47',
                text : 'Loose aggregate overlay systems'
            },
            {
                entry_id : 'Ss_30_14_80_48',
                text : 'Loose gravel paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_75',
                text : 'Sand play surfacing systems'
            },
            {
                entry_id : 'Ss_30_14_80_90',
                text : 'Unbound aggregate cycle paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_95',
                text : 'Woodchip and stone trim trail paving systems'
            },
            {
                entry_id : 'Ss_30_14_80_98',
                text : 'Woodchip and woodfibre surface paving systems'
            },
            {
                entry_id : 'Ss_30_14_90',
                text : 'Unit paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_11',
                text : 'Cellular plastics grass-filled paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_12',
                text : 'Cellular plastics gravel-filled paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_30',
                text : 'Flag and slab bound paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_32',
                text : 'Flag and slab paving overlay systems'
            },
            {
                entry_id : 'Ss_30_14_90_34',
                text : 'Flag and slab pedestal-supported paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_36',
                text : 'Flag and slab unbound paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_50',
                text : 'Mosaic paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_60',
                text : 'Permeable flag and slab paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_62',
                text : 'Permeable small unit paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_70',
                text : 'Rail track paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_75',
                text : 'Small unit bound paving systems'
            },
            {
                entry_id : 'Ss_30_14_90_80',
                text : 'Small unit paving overlay systems'
            },
            {
                entry_id : 'Ss_30_14_90_85',
                text : 'Small unit unbound paving systems'
            },
            {
                entry_id : 'Ss_30_20',
                text : 'Flooring and decking systems'
            },
            {
                entry_id : 'Ss_30_20_10',
                text : 'Board and rigid sheet floor systems'
            },
            {
                entry_id : 'Ss_30_20_10_10',
                text : 'Battened timber board floating floor systems'
            },
            {
                entry_id : 'Ss_30_20_10_15',
                text : 'Battened wood-based rigid sheet floating floor systems'
            },
            {
                entry_id : 'Ss_30_20_10_43',
                text : 'Joist-supported mineral-based sheet and board floor systems'
            },
            {
                entry_id : 'Ss_30_20_10_45',
                text : 'Joist-supported timber board floor systems'
            },
            {
                entry_id : 'Ss_30_20_10_50',
                text : 'Joist-supported wood-based rigid sheet floor systems'
            },
            {
                entry_id : 'Ss_30_20_10_95',
                text : 'Wood-based rigid sheet floating floor systems'
            },
            {
                entry_id : 'Ss_30_20_22',
                text : 'Demountable athletics floor systems'
            },
            {
                entry_id : 'Ss_30_20_30',
                text : 'External deck and boardwalk systems'
            },
            {
                entry_id : 'Ss_30_20_30_10',
                text : 'Boardwalk systems'
            },
            {
                entry_id : 'Ss_30_20_30_25',
                text : 'Decking systems'
            },
            {
                entry_id : 'Ss_30_20_70',
                text : 'Raised floor systems'
            },
            {
                entry_id : 'Ss_30_20_70_70',
                text : 'Raised access floor systems'
            },
            {
                entry_id : 'Ss_30_20_90',
                text : 'Wood and composite unit flooring systems'
            },
            {
                entry_id : 'Ss_30_20_90_15',
                text : 'Composition block flooring systems'
            },
            {
                entry_id : 'Ss_30_20_90_50',
                text : 'Mosaic parquet panel flooring systems'
            },
            {
                entry_id : 'Ss_30_20_90_95',
                text : 'Wood block flooring systems'
            },
            {
                entry_id : 'Ss_30_20_95',
                text : 'Wood strip and board fine flooring systems'
            },
            {
                entry_id : 'Ss_30_20_95_10',
                text : 'Battened wood floating floor systems'
            },
            {
                entry_id : 'Ss_30_20_95_15',
                text : 'Composite laminate floating floor systems'
            },
            {
                entry_id : 'Ss_30_20_95_20',
                text : 'Direct-fixed wood floor systems'
            },
            {
                entry_id : 'Ss_30_20_95_25',
                text : 'Direct-glued wood floor systems'
            },
            {
                entry_id : 'Ss_30_20_95_95',
                text : 'Wood floating floor systems'
            },
            {
                entry_id : 'Ss_30_25',
                text : 'Ceiling and soffit systems'
            },
            {
                entry_id : 'Ss_30_25_10',
                text : 'Board and sheet ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_10_10',
                text : 'Board suspended ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_10_26',
                text : 'External ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_10_28',
                text : 'External soffit systems'
            },
            {
                entry_id : 'Ss_30_25_10_35',
                text : 'Gypsum board suspended ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_10_52',
                text : 'Monolithic suspended ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_10_80',
                text : 'Soffit lining and beam casing systems'
            },
            {
                entry_id : 'Ss_30_25_22',
                text : 'Demountable suspended ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_22_01',
                text : 'Acoustic baffle suspended ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_22_30',
                text : 'Fabric membrane ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_22_47',
                text : 'Linear ceiling lining systems'
            },
            {
                entry_id : 'Ss_30_25_22_51',
                text : 'Modular suspended ceiling systems'
            },
            {
                entry_id : 'Ss_30_25_22_70',
                text : 'Raft or island suspended ceiling systems'
            },
            {
                entry_id : 'Ss_30_30',
                text : 'Roof and floor opening systems'
            },
            {
                entry_id : 'Ss_30_30_15',
                text : 'Concrete roof and floor glazing systems'
            },
            {
                entry_id : 'Ss_30_30_15_42',
                text : 'In situ concrete roof and floor glazing systems'
            },
            {
                entry_id : 'Ss_30_30_15_64',
                text : 'Precast concrete lift-out access roof and floor glazing systems'
            },
            {
                entry_id : 'Ss_30_30_15_65',
                text : 'Precast concrete roof and floor glazing systems'
            },
            {
                entry_id : 'Ss_30_30_30',
                text : 'Floor hatch systems'
            },
            {
                entry_id : 'Ss_30_30_34',
                text : 'Glass unit masonry roof light systems'
            },
            {
                entry_id : 'Ss_30_30_71',
                text : 'Roof hatch systems'
            },
            {
                entry_id : 'Ss_30_30_72',
                text : 'Rooflight and roof window systems'
            },
            {
                entry_id : 'Ss_30_30_72_21',
                text : 'Daylight pipe systems'
            },
            {
                entry_id : 'Ss_30_30_72_72',
                text : 'Rooflight systems'
            },
            {
                entry_id : 'Ss_30_30_72_73',
                text : 'Roof window systems'
            },
            {
                entry_id : 'Ss_30_30_73',
                text : 'Roof ventilator systems'
            },
            {
                entry_id : 'Ss_30_30_73_60',
                text : 'Passive roof natural ventilator systems'
            },
            {
                entry_id : 'Ss_30_30_73_72',
                text : 'Roof smoke ventilator systems'
            },
            {
                entry_id : 'Ss_30_34',
                text : 'Pavement opening systems'
            },
            {
                entry_id : 'Ss_30_34_03',
                text : 'Animal grid systems'
            },
            {
                entry_id : 'Ss_30_34_03_11',
                text : 'Cattle grid systems'
            },
            {
                entry_id : 'Ss_30_34_15',
                text : 'Pavement glazing systems'
            },
            {
                entry_id : 'Ss_30_34_15_15',
                text : 'Concrete pavement glazing systems'
            },
            {
                entry_id : 'Ss_30_34_15_64',
                text : 'Precast concrete lift-out access pavement glazing systems'
            },
            {
                entry_id : 'Ss_30_34_15_65',
                text : 'Precast concrete pavement glazing systems'
            },
            {
                entry_id : 'Ss_30_34_16',
                text : 'Pavement smoke venting systems'
            },
            {
                entry_id : 'Ss_30_34_16_15',
                text : 'Concrete pavement glazing smoke venting systems'
            },
            {
                entry_id : 'Ss_30_34_16_16',
                text : 'Concrete pavement non-glazed smoke venting systems'
            },
            {
                entry_id : 'Ss_30_34_16_64',
                text : 'Precast concrete pavement glazing smoke venting systems'
            },
            {
                entry_id : 'Ss_30_34_16_65',
                text : 'Precast concrete pavement non-glazed smoke venting systems'
            },
            {
                entry_id : 'Ss_30_36',
                text : 'Ceiling and soffit opening systems'
            },
            {
                entry_id : 'Ss_30_36_10',
                text : 'Ceiling hatch systems'
            },
            {
                entry_id : 'Ss_30_36_75',
                text : 'Soffit vent systems'
            },
            {
                entry_id : 'Ss_30_40',
                text : 'Roof and balcony covering and finish systems'
            },
            {
                entry_id : 'Ss_30_40_10',
                text : 'Board roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_10_30',
                text : 'Flat board roof deck systems'
            },
            {
                entry_id : 'Ss_30_40_10_60',
                text : 'Pitched board roof sarking systems'
            },
            {
                entry_id : 'Ss_30_40_30',
                text : 'Flat roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_07',
                text : 'Blue roof systems'
            },
            {
                entry_id : 'Ss_30_40_30_40',
                text : 'Liquid-applied cold roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_42',
                text : 'Liquid-applied inverted roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_43',
                text : 'Liquid-applied warm roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_50',
                text : 'Mastic asphalt cold roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_52',
                text : 'Mastic asphalt inverted roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_55',
                text : 'Mastic asphalt warm roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_70',
                text : 'Reinforced bitumen membrane cold roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_71',
                text : 'Reinforced bitumen membrane inverted roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_72',
                text : 'Reinforced bitumen membrane warm roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_78',
                text : 'Single layer sheet cold roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_79',
                text : 'Single layer sheet inverted roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_30_80',
                text : 'Single layer sheet warm roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_32',
                text : 'Framed glazed roof systems'
            },
            {
                entry_id : 'Ss_30_40_32_75',
                text : 'Sloping patent glazing systems'
            },
            {
                entry_id : 'Ss_30_40_34',
                text : 'Glass roofing systems'
            },
            {
                entry_id : 'Ss_30_40_34_15',
                text : 'Cantilevered structural glass beam canopy systems'
            },
            {
                entry_id : 'Ss_30_40_34_60',
                text : 'Point-fixed structural glass canopy systems'
            },
            {
                entry_id : 'Ss_30_40_34_65',
                text : 'Point-fixed structural glass roof systems'
            },
            {
                entry_id : 'Ss_30_40_34_70',
                text : 'Point-fixed structural glass soffit systems'
            },
            {
                entry_id : 'Ss_30_40_50',
                text : 'Metal sheet, fully supported roof and dormer covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_05',
                text : 'Aluminium sheet dormer covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_10',
                text : 'Aluminium sheet fully supported roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_12',
                text : 'Carbon steel sheet dormer covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_13',
                text : 'Carbon steel sheet fully supported roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_15',
                text : 'Copper sheet dormer covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_20',
                text : 'Copper sheet fully supported roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_40',
                text : 'Lead sheet dormer covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_45',
                text : 'Lead sheet fully supported roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_50',
                text : 'Lead-wrapped panel systems'
            },
            {
                entry_id : 'Ss_30_40_50_75',
                text : 'Stainless steel sheet dormer covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_80',
                text : 'Stainless steel sheet fully supported roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_90',
                text : 'Zinc sheet dormer covering systems'
            },
            {
                entry_id : 'Ss_30_40_50_95',
                text : 'Zinc sheet fully supported roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_55',
                text : 'Metal composite panel roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_55_10',
                text : 'Built up metal insulating sandwich panel roof systems'
            },
            {
                entry_id : 'Ss_30_40_55_50',
                text : 'Metal composite panel roofing systems'
            },
            {
                entry_id : 'Ss_30_40_55_54',
                text : 'Metal insulating sandwich panel roof systems'
            },
            {
                entry_id : 'Ss_30_40_55_55',
                text : 'Metal semi-composite panel roofing systems'
            },
            {
                entry_id : 'Ss_30_40_65',
                text : 'Profiled sheet self-supporting roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_65_30',
                text : 'Fibre cement profiled self-supporting sheet roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_65_50',
                text : 'Metal profiled sheet self-supporting roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_65_60',
                text : 'Plastics profiled sheet self-supporting roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_90',
                text : 'Tensile fabric roofing systems'
            },
            {
                entry_id : 'Ss_30_40_90_40',
                text : 'Internal single layer tensile fabric roofing systems'
            },
            {
                entry_id : 'Ss_30_40_90_50',
                text : 'Multilayer tensile fabric roofing systems'
            },
            {
                entry_id : 'Ss_30_40_90_75',
                text : 'Single layer tensile fabric roofing systems'
            },
            {
                entry_id : 'Ss_30_40_92',
                text : 'Thatch roof covering systems'
            },
            {
                entry_id : 'Ss_30_40_92_85',
                text : 'Thatch roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95',
                text : 'Unit roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_10',
                text : 'Bitumen membrane shingle roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_15',
                text : 'Ceramic slate roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_30',
                text : 'Fibre cement slate roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_40',
                text : 'Interlocking tile roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_50',
                text : 'Metal tile roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_55',
                text : 'Natural slate roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_60',
                text : 'Natural stone slate roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_65',
                text : 'Plain tile roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_70',
                text : 'Reconstituted slate roofing systems'
            },
            {
                entry_id : 'Ss_30_40_95_74',
                text : 'Roofing edges and junction systems'
            },
            {
                entry_id : 'Ss_30_40_95_95',
                text : 'Wood shake or shingle roofing systems'
            },
            {
                entry_id : 'Ss_30_42',
                text : 'Floor covering and finishing systems'
            },
            {
                entry_id : 'Ss_30_42_10',
                text : 'Calcium sulfate-based screed systems'
            },
            {
                entry_id : 'Ss_30_42_10_10',
                text : 'Bonded calcium sulfate-based screed systems'
            },
            {
                entry_id : 'Ss_30_42_10_30',
                text : 'Floating calcium sulfate-based screed systems'
            },
            {
                entry_id : 'Ss_30_42_10_90',
                text : 'Unbonded calcium sulfate-based screed systems'
            },
            {
                entry_id : 'Ss_30_42_15',
                text : 'Cementitious screed systems'
            },
            {
                entry_id : 'Ss_30_42_15_10',
                text : 'Bonded or partially bonded cementitious levelling screed systems'
            },
            {
                entry_id : 'Ss_30_42_15_15',
                text : 'Cementitious wearing screed systems'
            },
            {
                entry_id : 'Ss_30_42_15_30',
                text : 'Floating cementitious levelling screed systems'
            },
            {
                entry_id : 'Ss_30_42_15_90',
                text : 'Unbonded cementitious levelling screed systems'
            },
            {
                entry_id : 'Ss_30_42_20',
                text : 'Deck covering systems'
            },
            {
                entry_id : 'Ss_30_42_30',
                text : 'Floor paint systems'
            },
            {
                entry_id : 'Ss_30_42_30_30',
                text : 'Floor coating systems'
            },
            {
                entry_id : 'Ss_30_42_32',
                text : 'Floor tiling systems'
            },
            {
                entry_id : 'Ss_30_42_32_30',
                text : 'External floor tiling systems'
            },
            {
                entry_id : 'Ss_30_42_32_40',
                text : 'Internal floor tiling systems'
            },
            {
                entry_id : 'Ss_30_42_40',
                text : 'Ice rink floor systems'
            },
            {
                entry_id : 'Ss_30_42_52',
                text : 'Mastic asphalt flooring and floor underlay systems'
            },
            {
                entry_id : 'Ss_30_42_52_50',
                text : 'Mastic asphalt flooring systems'
            },
            {
                entry_id : 'Ss_30_42_52_52',
                text : 'Mastic asphalt floor underlay systems'
            },
            {
                entry_id : 'Ss_30_42_54',
                text : 'Monolithic flooring systems'
            },
            {
                entry_id : 'Ss_30_42_54_75',
                text : 'Resin flooring systems'
            },
            {
                entry_id : 'Ss_30_42_56',
                text : 'Natural stone flooring systems'
            },
            {
                entry_id : 'Ss_30_42_56_56',
                text : 'Natural stone slab flooring systems'
            },
            {
                entry_id : 'Ss_30_42_56_57',
                text : 'Natural stone tile flooring systems'
            },
            {
                entry_id : 'Ss_30_42_72',
                text : 'Resilient and textile floor covering systems'
            },
            {
                entry_id : 'Ss_30_42_72_05',
                text : 'Adhesive-fixed rolled carpet systems'
            },
            {
                entry_id : 'Ss_30_42_72_10',
                text : 'Carpet tile systems'
            },
            {
                entry_id : 'Ss_30_42_72_25',
                text : 'Edge-fixed carpet systems'
            },
            {
                entry_id : 'Ss_30_42_72_72',
                text : 'Resilient sheet floor covering systems'
            },
            {
                entry_id : 'Ss_30_42_72_75',
                text : 'Resilient tile floor covering systems'
            },
            {
                entry_id : 'Ss_30_42_90',
                text : 'Terrazzo floor tiling and screed systems'
            },
            {
                entry_id : 'Ss_30_42_90_90',
                text : 'Terrazzo floor covering systems'
            },
            {
                entry_id : 'Ss_30_45',
                text : 'Pavement covering and finishing systems'
            },
            {
                entry_id : 'Ss_30_47',
                text : 'Ceiling and soffit covering and finishing systems'
            },
            {
                entry_id : 'Ss_30_60',
                text : 'Roof, floor and paving accessory systems'
            },
            {
                entry_id : 'Ss_30_60_30',
                text : 'Floor feature systems'
            },
            {
                entry_id : 'Ss_30_60_30_26',
                text : 'Entrance matting systems'
            },
            {
                entry_id : 'Ss_30_60_45',
                text : 'Kerb and traffic separation systems'
            },
            {
                entry_id : 'Ss_30_60_45_03',
                text : 'Asphalt kerb systems'
            },
            {
                entry_id : 'Ss_30_60_45_15',
                text : 'Concrete kerb systems'
            },
            {
                entry_id : 'Ss_30_60_45_20',
                text : 'Demarcation paving systems'
            },
            {
                entry_id : 'Ss_30_60_45_45',
                text : 'Light-duty kerb systems'
            },
            {
                entry_id : 'Ss_30_60_45_55',
                text : 'Multi unit traffic calming systems'
            },
            {
                entry_id : 'Ss_30_60_45_65',
                text : 'Precast kerb systems'
            },
            {
                entry_id : 'Ss_30_60_45_80',
                text : 'Single unit traffic calming systems'
            },
            {
                entry_id : 'Ss_30_60_45_85',
                text : 'Traffic kerb systems'
            },
            {
                entry_id : 'Ss_30_60_45_86',
                text : 'Traffic separation systems'
            },
            {
                entry_id : 'Ss_30_60_70',
                text : 'Roof feature systems'
            },
            {
                entry_id : 'Ss_30_75',
                text : 'Roof, floor and paving drainage systems'
            },
            {
                entry_id : 'Ss_30_75_40',
                text : 'Surface drainage channel systems'
            },
            {
                entry_id : 'Ss_30_75_40_30',
                text : 'Footway grating systems'
            },
            {
                entry_id : 'Ss_30_75_40_50',
                text : 'Multi unit surface drainage channel systems'
            },
            {
                entry_id : 'Ss_30_75_40_75',
                text : 'Single unit surface drainage channel systems'
            },
            {
                entry_id : 'Ss_30_75_50',
                text : 'Metal sheet gutter lining systems'
            },
            {
                entry_id : 'Ss_30_75_50_05',
                text : 'Aluminium sheet gutter lining systems'
            },
            {
                entry_id : 'Ss_30_75_50_11',
                text : 'Carbon steel sheet gutter lining systems'
            },
            {
                entry_id : 'Ss_30_75_50_15',
                text : 'Copper sheet gutter lining systems'
            },
            {
                entry_id : 'Ss_30_75_50_45',
                text : 'Lead sheet gutter lining systems'
            },
            {
                entry_id : 'Ss_30_75_50_75',
                text : 'Stainless steel sheet gutter lining systems'
            },
            {
                entry_id : 'Ss_30_75_50_95',
                text : 'Zinc sheet gutter lining systems'
            },
            {
                entry_id : 'Ss_30_95',
                text : 'Temporary roof, floor and paving systems'
            },
            {
                entry_id : 'Ss_32',
                text : 'Damp-proofing, waterproofing and plaster-finishing systems'
            },
            {
                entry_id : 'Ss_32_20',
                text : 'Damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_20',
                text : 'Damp-proof course renewal and insertion systems'
            },
            {
                entry_id : 'Ss_32_20_20_15',
                text : 'Chemical injection damp-proof course systems'
            },
            {
                entry_id : 'Ss_32_20_20_47',
                text : 'Lead damp-proof course systems'
            },
            {
                entry_id : 'Ss_32_20_20_62',
                text : 'Physical insertion horizontal damp-proof course systems'
            },
            {
                entry_id : 'Ss_32_20_20_63',
                text : 'Physical insertion horizontal or stepped cavity tray systems'
            },
            {
                entry_id : 'Ss_32_20_20_64',
                text : 'Physical insertion proprietary rising damp remedial systems'
            },
            {
                entry_id : 'Ss_32_20_20_65',
                text : 'Physical insertion vertical damp-proof course systems'
            },
            {
                entry_id : 'Ss_32_20_30',
                text : 'Floor damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_10',
                text : 'Cementitious mortar slurry floor damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_15',
                text : 'Cold-applied liquid floor damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_30',
                text : 'Fully bonded bitumen membrane overslab damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_35',
                text : 'Fully bonded bitumen membrane underslab damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_40',
                text : 'Hot-applied liquid floor damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_45',
                text : 'Loose-laid bitumen membrane overslab damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_46',
                text : 'Loose-laid bitumen membrane underslab damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_48',
                text : 'Loose-laid polyethylene membrane oversite damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_50',
                text : 'Loose-laid polyethylene membrane overslab damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_51',
                text : 'Loose-laid polyethylene membrane underslab damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_55',
                text : 'Mastic asphalt floor damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_60',
                text : 'Plastics studded membrane floor damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_65',
                text : 'Pre-applied anchored membrane underslab damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_30_75',
                text : 'Self-adhesive bitumen membrane floor damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_95',
                text : 'Wall damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_95_10',
                text : 'Cementitious mortar slurry internal wall damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_95_15',
                text : 'Cold-applied liquid internal wall damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_20_95_65',
                text : 'Plastics studded membrane internal wall damp-proofing systems'
            },
            {
                entry_id : 'Ss_32_30',
                text : 'Flashing and weathering systems'
            },
            {
                entry_id : 'Ss_32_30_30',
                text : 'Flashing systems'
            },
            {
                entry_id : 'Ss_32_30_30_03',
                text : 'Aluminium flashing systems'
            },
            {
                entry_id : 'Ss_32_30_30_11',
                text : 'Carbon steel flashing systems'
            },
            {
                entry_id : 'Ss_32_30_30_14',
                text : 'Copper flashing systems'
            },
            {
                entry_id : 'Ss_32_30_30_47',
                text : 'Lead flashing systems'
            },
            {
                entry_id : 'Ss_32_30_30_84',
                text : 'Stainless steel flashing systems'
            },
            {
                entry_id : 'Ss_32_30_30_98',
                text : 'Zinc flashing systems'
            },
            {
                entry_id : 'Ss_32_30_90',
                text : 'Weathering  systems'
            },
            {
                entry_id : 'Ss_32_30_90_04',
                text : 'Aluminium weathering systems'
            }
        ];
        data.forEach(x => x.text = x.entry_id + '-' + x.text);
        this.entries$.next(data);
    }
}
