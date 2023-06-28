import {DataDictionaryEntry} from './data-dictionary-entry';
import {Airs} from "./airs";
export interface Asset {
    id: string;
    data_dictionary_entry: DataDictionaryEntry;
    name: string;
    airs: Airs[];
}

