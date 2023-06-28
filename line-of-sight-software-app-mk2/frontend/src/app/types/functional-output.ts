import {DataDictionaryEntry} from "./data-dictionary-entry";
import {Firs} from './firs'
export interface FunctionalOutput {
    id: string;
    data_dictionary_entry: DataDictionaryEntry;
    name: string;  // only required to make this assignable to IMultiSelectOption
    firs: Firs[];
    assets: string[];
}
