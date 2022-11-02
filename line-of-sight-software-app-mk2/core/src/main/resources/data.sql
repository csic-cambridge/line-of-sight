insert into asset (id, data_dictionary_entry_id)
values
    ('5799ee92-f25c-4173-aecd-dfa871719065', 'Ss_15_10_33_34'),
    ('0ee6c05d-c82c-494f-b13c-82f8d64178a1', 'Ss_15_10_78'),
    ('6dcb0cd6-9f63-407c-8d4c-c6d29a5e4a75', 'Ss_25_13_50_54'),
    ('b2044e60-b5a4-41dc-9a5d-f7577c91bdc0', 'Ss_25_14_67_12'),
    ('f6148b21-5a2c-427a-ac52-aee3a4181c8f', 'Ss_25_16_94_95'),
    ('27217551-7641-4e1b-a297-11b49adb91b4', 'Ss_25_20_90_15'),
    ('16b4a26c-dd38-4674-a197-049d7385fa82', 'Ss_25_38_20_30'),
    ('4a7272c6-70d1-491e-94c0-6f4bdae29eb5', 'Ss_30_25_10_10'),
    ('5417ce9a-a329-42a1-90b2-a1badfd13fcb', 'Ss_32_30_90_12'),
    ('e6d74d50-9bc7-4aff-98eb-d2480b2d98ae', 'Ss_40_10_25_25'),
    ('ae57eb75-2872-4d5c-a1e8-a385f3de825b', 'Ss_40_10_30_36'),
    ('ad4d9d3f-d428-4038-a158-b0538a84632f', 'Ss_40_10_30_70'),
    ('ead84c97-a1a4-4b9a-a286-b2b36cb609ea', 'Ss_45_75_20_16'),
    ('68e47638-4bfb-4728-ad9e-e2e4bec22595', 'Ss_45_75_44'),
    ('50740e2b-a2b6-491d-a92f-babf0959784a', 'Ss_50_75_58_07');

insert into airs (asset_id, airs)
values
    ('5799ee92-f25c-4173-aecd-dfa871719065', 'AIR #1'),
    ('5799ee92-f25c-4173-aecd-dfa871719065', 'AIR #2'),
    ('5799ee92-f25c-4173-aecd-dfa871719065', 'AIR #3'),
    ('0ee6c05d-c82c-494f-b13c-82f8d64178a1', 'AIR #4'),
    ('0ee6c05d-c82c-494f-b13c-82f8d64178a1', 'AIR #5'),
    ('0ee6c05d-c82c-494f-b13c-82f8d64178a1', 'AIR #6'),
    ('6dcb0cd6-9f63-407c-8d4c-c6d29a5e4a75', 'AIR #7'),
    ('6dcb0cd6-9f63-407c-8d4c-c6d29a5e4a75', 'AIR #8'),
    ('6dcb0cd6-9f63-407c-8d4c-c6d29a5e4a75', 'AIR #9'),
    ('27217551-7641-4e1b-a297-11b49adb91b4', 'AIR #10');

insert into functional_output (id, data_dictionary_entry_id)
values
    ('0d72e486-74cf-45bc-b11e-cbb7a16b698f', 'EF_35_10_30'),
    ('91990373-dc68-4705-ace8-2a4d36d5bcce', 'EF_37_17_15'),
    ('fbdd7a8a-57a6-46d1-a2d1-af57943fe5cc', 'EF_70_80');

insert into firs (fo_id, firs)
values
    ('0d72e486-74cf-45bc-b11e-cbb7a16b698f', 'FIR #1'),
    ('0d72e486-74cf-45bc-b11e-cbb7a16b698f', 'FIR #2'),
    ('91990373-dc68-4705-ace8-2a4d36d5bcce', 'FIR #3');

insert into fo_assets (fo_id, asset_id)
values
    ('0d72e486-74cf-45bc-b11e-cbb7a16b698f', '5799ee92-f25c-4173-aecd-dfa871719065'),
    ('0d72e486-74cf-45bc-b11e-cbb7a16b698f', '27217551-7641-4e1b-a297-11b49adb91b4'),
    ('91990373-dc68-4705-ace8-2a4d36d5bcce', '5799ee92-f25c-4173-aecd-dfa871719065');

insert into functional_requirement (id, name)
values
    ('1cfda9b4-1466-4695-8ab2-c19848fa4542', 'FR #1'),
    ('884deff4-9a42-4436-8ef2-561777a4f3fd', 'FR #2'),
    ('2853000a-ec64-4d5b-9dcd-f73d8a79e1cc', 'FR #3'),
    ('53f26727-63ae-4570-a91a-074f3fbdb110', 'FR #4');

insert into fr_fo(fr_id, fo_id)
values
    ('1cfda9b4-1466-4695-8ab2-c19848fa4542', '0d72e486-74cf-45bc-b11e-cbb7a16b698f'),
    ('1cfda9b4-1466-4695-8ab2-c19848fa4542', '91990373-dc68-4705-ace8-2a4d36d5bcce'),
    ('1cfda9b4-1466-4695-8ab2-c19848fa4542', 'fbdd7a8a-57a6-46d1-a2d1-af57943fe5cc'),
    ('2853000a-ec64-4d5b-9dcd-f73d8a79e1cc', '0d72e486-74cf-45bc-b11e-cbb7a16b698f');

insert into organisational_objective(id, name)
values
    ('064fecac-d5e8-4ee5-8e96-d4bc63cc95ed', 'Objective #1'),
    ('fc2aae01-5543-4218-adda-99f459e61295', 'Objective #2'),
    ('ee183d1b-f9f4-4d7d-8c0a-53f81b198ced', 'Objective #3');

insert into oirs(oo_id, oirs)
values
    ('064fecac-d5e8-4ee5-8e96-d4bc63cc95ed', 'OIR #1'),
    ('064fecac-d5e8-4ee5-8e96-d4bc63cc95ed', 'OIR #2'),
    ('fc2aae01-5543-4218-adda-99f459e61295', 'OIR #3'),
    ('fc2aae01-5543-4218-adda-99f459e61295', 'OIR #4');

insert into oo_frs(oo_id, fr_id)
values
    ('064fecac-d5e8-4ee5-8e96-d4bc63cc95ed', '1cfda9b4-1466-4695-8ab2-c19848fa4542'),
    ('fc2aae01-5543-4218-adda-99f459e61295', '1cfda9b4-1466-4695-8ab2-c19848fa4542'),
    ('fc2aae01-5543-4218-adda-99f459e61295', '884deff4-9a42-4436-8ef2-561777a4f3fd'),
    ('ee183d1b-f9f4-4d7d-8c0a-53f81b198ced', '1cfda9b4-1466-4695-8ab2-c19848fa4542');
