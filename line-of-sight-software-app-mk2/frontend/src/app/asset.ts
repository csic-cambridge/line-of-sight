import {DataDictionaryEntry} from "./data-dictionary-entry";

export interface Asset {
    id: string;
    data_dictionary_entry: DataDictionaryEntry
    airs: string[];
}
