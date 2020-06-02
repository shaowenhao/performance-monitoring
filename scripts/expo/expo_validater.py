import pandas
import xlrd
import xml.etree.ElementTree as ET

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

MAPPPING = {
    'Substation': 'Substation',
    'Bay': '*/Bay',
    'Analog': '*/*/Analog',
    'AnalogValue': '*/*/*/AnalogValue',
    'Command': '*/*/*/Command',
    'Discrete': '*/*/Discrete',
    'DiscreteValue': '*/*/*/DiscreteValue',
    'SetPoint': '*/*/*/SetPoint'
}

CACHE = {}

root = ET.parse('sdl-1303/expo_20200519.xml').getroot()
# print(root)
sub_root = root.findall('Instances/Parent/sysNetworkCategory/sysNetSubstations')[0]
# print(sub_root)

# print(type_tag)
# for element in type_tag.getchildren():

def get_ele_list(cim):
    if cim in CACHE:
        return CACHE.get(cim)
    else:
        ele_list = sub_root.findall(MAPPPING[cim])
        CACHE[cim] = ele_list
        return ele_list

def get_total_eles():
    total = 0
    for key, value in CACHE.items():
        total = total + len(value)
    return total

for index, row in df_selected.iterrows():
    cim = row['SP5_CIM']
    ele_list = get_ele_list(cim)
    result = list(filter(lambda ele: ele.get('Siid') == str(row['Siid']) and ele.get('Name') == row['EXPO'] and ele.get('RdfId') == row['RdfId'], ele_list))
    if len(result) != 1:
        print("do not find in xml with {}".format(row))

print('total {} in xml'.format(get_total_eles()))


print("Finish!")
