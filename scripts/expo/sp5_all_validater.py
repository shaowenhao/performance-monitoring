import pandas
import xlrd
import json

def load_data(path):
    with open(path, 'r', encoding='utf-8') as stream:
        try:
            data = json.load(stream)
        except Exception as exc:
            print(exc)
            print(
                "Failed to load data from {} , exception is : \n {} \n".format(path, exc))
            raise
        else:
            return data



# pip install xlrd panndas
df = pandas.read_excel('sdl-1303/EXPO_mark_CIM.xlsx')
#print the column names
# print(df.columns)
#get the values for a given column
# values = df['Arm_id'].values
#get a data frame with selected columns
FORMAT = ['RdfId', 'Siid', 'EXPO', 'SP5_CIM']
df_selected = df[FORMAT]
# print(df_selected)

kg_dict = load_data('sdl-1315/sp5_all.json')

kg_entities = kg_dict['data'].get('entities')
kg_relations = kg_dict['data'].get('relations')


for index, row in df_selected.iterrows():
    result = list(filter(lambda ele: ele['schemas'].get('Siid') == str(row['Siid']) and ele['schemas'].get('Name') == row['EXPO'] and ele['schemas'].get('RdfId') == row['RdfId'], kg_entities))
    if len(result) != 1:
        print("do not find in json with {}".format(row))
    else:
        instance = result[0]
        if instance['schemas'].get('metadata-node-type') != 'instance':
            print("metadata-node-type is not instance: {}".format(instance))
        else:
            relation = next((item for item in kg_relations if item['label'] == 'Instance_Of' and item['in']['id'] == instance['id'] and item['in']['label'] == instance['name']), None)
            if relation:
                if relation['out']['label'] != row['SP5_CIM']:
                    print("entity type not same, expect {}, actually is {}".format(row['SP5_CIM'], relation['out']['label']))
                    print(relation)
            else:
                print("can not find entity relation for instance: {}".format(instance))
        in_relation = list(filter(lambda item: item['label'] != 'Instance_Of' and item['in']['id'] == instance['id'] and item['in']['label'] == instance['name'], kg_relations))
        out_relation = list(filter(lambda item: item['label'] != 'Instance_Of' and item['out']['id'] == instance['id'] and item['out']['label'] == instance['name'], kg_relations))
        print('relation')
print("Finish!")
