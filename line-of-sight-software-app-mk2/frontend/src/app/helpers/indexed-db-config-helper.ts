import { DBConfig } from 'ngx-indexed-db';



export class IndexedDbConfigHelperHelper {
    static dbConfig: DBConfig  = {
        name: 'LineOfSightDb',
        version: 1,
        objectStoresMeta: [{
            store: 'airs',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'asset_id', keypath: 'asset_id', options: { unique: false } },
                { name: 'airs', keypath: 'airs', options: { unique: false } }
            ]
        }, {
            store: 'asset',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'project_id', keypath: 'project_id', options: { unique: false } },
                { name: 'data_dictionary_entry_id', keypath: 'data_dictionary_entry_id', options: { unique: false } }
            ]
        }, {
            store: 'asset_dictionary',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'name', keypath: 'name', options: { unique: false } }
            ]
        }, {
            store: 'asset_dictionary_entry',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'text', keypath: 'text', options: { unique: false } },
                { name: 'entry_id', keypath: 'entry_id', options: { unique: false } },
                { name: 'asset_dictionary_id', keypath: 'asset_dictionary_id', options: { unique: false } }
            ]
        }, {
            store: 'deleted_oir',
            storeConfig: { keyPath: ['oir_id', 'project_id'], autoIncrement: false },
            storeSchema: []
        }, {
            store: 'firs',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'fo_id', keypath: 'fo_id', options: { unique: false } },
                { name: 'firs', keypath: 'firs', options: { unique: false } }
            ]
        }, {
            store: 'fo_assets',
            storeConfig: { keyPath: ['asset_id', 'fo_id'], autoIncrement: false },
            storeSchema: []
        }, {
            store: 'fr_fo',
            storeConfig: { keyPath: ['fo_id', 'fr_id'], autoIncrement: false },
            storeSchema: []
        }, {
            store: 'functional_output',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'project_id', keypath: 'project_id', options: { unique: false } },
                { name: 'data_dictionary_entry_id', keypath: 'data_dictionary_entry_id', options: { unique: false } }
            ]
        }, {
            store: 'functional_output_dictionary',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'name', keypath: 'name', options: { unique: false } }
            ]
        }, {
            store: 'functional_output_dictionary_entry',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'text', keypath: 'text', options: { unique: false } },
                { name: 'entry_id', keypath: 'entry_id', options: { unique: false } },
                { name: 'fo_dictionary_id', keypath: 'fo_dictionary_id', options: { unique: false } }
            ]
        }, {
            store: 'functional_requirement',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'name', keypath: 'name', options: { unique: false } },
                { name: 'project_id', keypath: 'project_id', options: { unique: false } }
            ]
        }, {
            store: 'oirs',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'oo_id', keypath: 'oo_id', options: { unique: false } },
                { name: 'oirs', keypath: 'oirs', options: { unique: false } }
            ]
        }, {
            store: 'oo_version',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'oo_id', keypath: 'oo_id', options: { unique: false } },
                { name: 'name', keypath: 'name', options: { unique: false } },
                { name: 'date_created', keypath: 'date_created', options: { unique: false } }
            ]
        }, {
            store: 'organisational_objective',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'is_deleted', keypath: 'is_deleted', options: { unique: false } }
            ]
        }, {
            store: 'poo_frs',
            storeConfig: { keyPath: ['fr_id', 'poo_id'], autoIncrement: false },
            storeSchema: []
        }, {
            store: 'project',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'name', keypath: 'name', options: { unique: false } },
                { name: 'fo_dd_id', keypath: 'fo_dd_id', options: { unique: false } },
                { name: 'asset_dd_id', keypath: 'asset_dd_id', options: { unique: false } }
            ]
        }, {
            store: 'project_organisational_objective',
            storeConfig: { keyPath: 'id', autoIncrement: false },
            storeSchema: [
                { name: 'project_id', keypath: 'project_id', options: { unique: false } },
                { name: 'oov_id', keypath: 'oov_id', options: { unique: false } }
            ]
        }]
    };
}
