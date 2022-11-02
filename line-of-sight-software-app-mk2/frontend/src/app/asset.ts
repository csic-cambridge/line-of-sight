import {DataDictionaryEntry} from "./data-dictionary-entry";

export interface Asset {
    id: string;
    data_dictionary_entry: DataDictionaryEntry
    name: string;
    airs: string[];
}

