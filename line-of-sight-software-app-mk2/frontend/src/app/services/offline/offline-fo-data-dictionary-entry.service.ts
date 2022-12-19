import { Injectable } from '@angular/core';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';
import {Observable, of} from 'rxjs';
import {BaseFunctionalOutputDictionaryEntryService} from '../base/base-functional-output-dictionary-entry-service';

@Injectable({
  providedIn: 'root'
})
export class OfflineFoDataDictionaryEntryService extends BaseFunctionalOutputDictionaryEntryService {

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
                entry_id : 'EF_15',
                text : 'Earthworks and remediation'
            },
            {
                entry_id : 'EF_15_10',
                text : 'Groundworks and earthworks'
            },
            {
                entry_id : 'EF_15_30',
                text : 'Remediation, repair and renovation'
            },
            {
                entry_id : 'EF_20',
                text : 'Structural elements'
            },
            {
                entry_id : 'EF_20_05',
                text : 'Substructure'
            },
            {
                entry_id : 'EF_20_05_30',
                text : 'Foundations'
            },
            {
                entry_id : 'EF_20_10',
                text : 'Superstructure'
            },
            {
                entry_id : 'EF_20_10_15',
                text : 'Composite structures'
            },
            {
                entry_id : 'EF_20_10_30',
                text : 'Framed structures'
            },
            {
                entry_id : 'EF_20_10_50',
                text : 'Membrane structures'
            },
            {
                entry_id : 'EF_20_10_75',
                text : 'Shell structures'
            },
            {
                entry_id : 'EF_20_10_80',
                text : 'Solid structures'
            },
            {
                entry_id : 'EF_20_50',
                text : 'Bridge structures'
            },
            {
                entry_id : 'EF_20_50_01',
                text : 'Abutments'
            },
            {
                entry_id : 'EF_20_50_07',
                text : 'Bearings'
            },
            {
                entry_id : 'EF_20_50_10',
                text : 'Bridge decks'
            },
            {
                entry_id : 'EF_20_50_64',
                text : 'Piers'
            },
            {
                entry_id : 'EF_20_50_82',
                text : 'Span'
            },
            {
                entry_id : 'EF_25',
                text : 'Wall and barrier elements'
            },
            {
                entry_id : 'EF_25_10',
                text : 'Walls'
            },
            {
                entry_id : 'EF_25_30',
                text : 'Doors and windows'
            },
            {
                entry_id : 'EF_25_55',
                text : 'Barriers'
            },
            {
                entry_id : 'EF_30',
                text : 'Roofs, floor and paving elements'
            },
            {
                entry_id : 'EF_30_10',
                text : 'Roofs'
            },
            {
                entry_id : 'EF_30_20',
                text : 'Floors'
            },
            {
                entry_id : 'EF_30_25',
                text : 'Ceilings and soffits'
            },
            {
                entry_id : 'EF_30_30',
                text : 'Decks'
            },
            {
                entry_id : 'EF_30_60',
                text : 'Pavements'
            },
            {
                entry_id : 'EF_30_60_60',
                text : 'Pedestrian paving'
            },
            {
                entry_id : 'EF_30_60_95',
                text : 'Vehicular paving'
            },
            {
                entry_id : 'EF_35',
                text : 'Stairs and ramps'
            },
            {
                entry_id : 'EF_35_10',
                text : 'Stairs'
            },
            {
                entry_id : 'EF_35_10_30',
                text : 'External stairs'
            },
            {
                entry_id : 'EF_35_10_40',
                text : 'Internal stairs'
            },
            {
                entry_id : 'EF_35_20',
                text : 'Ramps'
            },
            {
                entry_id : 'EF_35_20_30',
                text : 'External ramps'
            },
            {
                entry_id : 'EF_35_20_40',
                text : 'Internal ramps'
            },
            {
                entry_id : 'EF_37',
                text : 'Tunnel, vessel and tower elements'
            },
            {
                entry_id : 'EF_37_16',
                text : 'Vessels and trenches'
            },
            {
                entry_id : 'EF_37_16_90',
                text : 'Trenches'
            },
            {
                entry_id : 'EF_37_16_94',
                text : 'Vessels'
            },
            {
                entry_id : 'EF_37_17',
                text : 'Towers, chimneys and masts'
            },
            {
                entry_id : 'EF_37_17_15',
                text : 'Chimneys'
            },
            {
                entry_id : 'EF_37_17_35',
                text : 'Gantries'
            },
            {
                entry_id : 'EF_37_17_50',
                text : 'Masts'
            },
            {
                entry_id : 'EF_37_17_70',
                text : 'Pylons'
            },
            {
                entry_id : 'EF_37_17_90',
                text : 'Towers'
            },
            {
                entry_id : 'EF_37_50',
                text : 'Tunnels and shafts'
            },
            {
                entry_id : 'EF_40',
                text : 'Signage, fittings, furnishings and equipment'
            },
            {
                entry_id : 'EF_40_10',
                text : 'Signage'
            },
            {
                entry_id : 'EF_40_20',
                text : 'Fittings'
            },
            {
                entry_id : 'EF_40_30',
                text : 'Furnishings'
            },
            {
                entry_id : 'EF_40_40',
                text : 'Equipment'
            },
            {
                entry_id : 'EF_45',
                text : 'Flora and fauna elements'
            },
            {
                entry_id : 'EF_45_03',
                text : 'Aquatic fauna elements'
            },
            {
                entry_id : 'EF_45_05',
                text : 'Aquatic flora elements'
            },
            {
                entry_id : 'EF_45_20',
                text : 'Grass and meadow elements'
            },
            {
                entry_id : 'EF_45_45',
                text : 'Land fauna elements'
            },
            {
                entry_id : 'EF_45_90',
                text : 'Tree, shrub and herbaceous plant elements'
            },
            {
                entry_id : 'EF_50',
                text : 'Waste disposal functions'
            },
            {
                entry_id : 'EF_50_10',
                text : 'Gas waste collection'
            },
            {
                entry_id : 'EF_50_20',
                text : 'Wet waste collection'
            },
            {
                entry_id : 'EF_50_30',
                text : 'Above-ground drainage collection'
            },
            {
                entry_id : 'EF_50_35',
                text : 'Below-ground drainage collection'
            },
            {
                entry_id : 'EF_50_40',
                text : 'Dry waste collection'
            },
            {
                entry_id : 'EF_50_50',
                text : 'Gas waste treatment and disposal'
            },
            {
                entry_id : 'EF_50_60',
                text : 'Wet waste treatment and disposal'
            },
            {
                entry_id : 'EF_50_70',
                text : 'Drainage treatment and disposal'
            },
            {
                entry_id : 'EF_50_75',
                text : 'Wastewater treatment and disposal'
            },
            {
                entry_id : 'EF_50_80',
                text : 'Dry waste treatment and disposal'
            },
            {
                entry_id : 'EF_55',
                text : 'Piped supply functions'
            },
            {
                entry_id : 'EF_55_05',
                text : 'Gas extraction and treatment'
            },
            {
                entry_id : 'EF_55_10',
                text : 'Liquid fuel extraction and treatment'
            },
            {
                entry_id : 'EF_55_15',
                text : 'Water extraction and treatment'
            },
            {
                entry_id : 'EF_55_20',
                text : 'Gas supply'
            },
            {
                entry_id : 'EF_55_30',
                text : 'Fire-extinguishing supply'
            },
            {
                entry_id : 'EF_55_40',
                text : 'Steam supply'
            },
            {
                entry_id : 'EF_55_50',
                text : 'Liquid fuel supply'
            },
            {
                entry_id : 'EF_55_60',
                text : 'Process liquid supply'
            },
            {
                entry_id : 'EF_55_70',
                text : 'Water supply'
            },
            {
                entry_id : 'EF_55_90',
                text : 'Piped solids supply'
            },
            {
                entry_id : 'EF_60',
                text : 'Heating, cooling and refrigeration functions'
            },
            {
                entry_id : 'EF_60_30',
                text : 'Rail and paving heating'
            },
            {
                entry_id : 'EF_60_40',
                text : 'Space heating and cooling'
            },
            {
                entry_id : 'EF_60_60',
                text : 'Refrigeration'
            },
            {
                entry_id : 'EF_60_80',
                text : 'Drying'
            },
            {
                entry_id : 'EF_65',
                text : 'Ventilation and air conditioning functions'
            },
            {
                entry_id : 'EF_65_40',
                text : 'Ventilation'
            },
            {
                entry_id : 'EF_65_80',
                text : 'Air conditioning'
            },
            {
                entry_id : 'EF_70',
                text : 'Electrical power and lighting functions'
            },
            {
                entry_id : 'EF_70_10',
                text : 'Electrical power generation'
            },
            {
                entry_id : 'EF_70_30',
                text : 'Electricity distribution and transmission'
            },
            {
                entry_id : 'EF_70_80',
                text : 'Lighting'
            },
            {
                entry_id : 'EF_75',
                text : 'Communications, security, safety and protection functions'
            },
            {
                entry_id : 'EF_75_10',
                text : 'Communication'
            },
            {
                entry_id : 'EF_75_30',
                text : 'Signalling'
            },
            {
                entry_id : 'EF_75_40',
                text : 'Security'
            },
            {
                entry_id : 'EF_75_50',
                text : 'Safety and protection'
            },
            {
                entry_id : 'EF_75_60',
                text : 'Environmental safety'
            },
            {
                entry_id : 'EF_75_70',
                text : 'Control and management'
            },
            {
                entry_id : 'EF_75_80',
                text : 'Protection'
            },
            {
                entry_id : 'EF_80',
                text : 'Transport functions'
            },
            {
                entry_id : 'EF_80_10',
                text : 'Cable transport'
            },
            {
                entry_id : 'EF_80_20',
                text : 'Conveyors'
            },
            {
                entry_id : 'EF_80_30',
                text : 'Cranes and hoists'
            },
            {
                entry_id : 'EF_80_50',
                text : 'Lifts'
            },
            {
                entry_id : 'EF_80_70',
                text : 'Rail tracks'
            }
        ];
        data.forEach(x => x.text = x.entry_id + '-' + x.text);
        this.entries$.next(data);
    }
}
