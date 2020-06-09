import re
import xml.etree.ElementTree as ET

root = ET.parse('sdl-1317/EXPO_test_20200603_update1.xml').getroot()

graph_root = ET.parse('sdl-1317/graph-editor-2-expo-instance.xml').getroot()
# print(root)
objects = root.findall('root/object')
graph_nodes = graph_root.findall('graph/node')
graph_edges = graph_root.findall('graph/edge')

def get_node(id):
    for n in graph_nodes:
        # sss = n.find('.//data[@key="Siid"]')
        # if sss:
        #     print('-------')
        #     print(sss)
        datas = n.findall('data')
        for d in datas:
            for k,v in d.attrib.items():
                if v == 'idV':
                    if id == d.text:
                        return n
    print("not find node with id {}".format(id))

def get_siid_node(given_siid):
    for n in graph_nodes:
        datas = n.findall('data')
        for d in datas:
            for k,v in d.attrib.items():
                if v == 'Siid':
                    if given_siid == d.text:
                        return n
    print("not find node with siid {}".format(given_siid))

def check_if_edge_exist(source_id, target_id):
    result = False
    count = 0
    for e in graph_edges:
        if e.attrib.get('source') == source_id and e.attrib.get('target') == target_id:
            result = True
            count = count + 1
    if result != True or count != 1:
        print("ERROR {} {} {} {}".format(result, count, source_id, target_id))
        return False
    return True

for o in objects:
    # print(o.attrib)
    for k, v in o.attrib.items():
        if 'siid' in v:
            # print(k, v)
            siid = re.findall("siid *[^\w ] *(.*)", v)[0].strip()
            source_node = get_node(o.get('id'))
            target_node = get_siid_node(siid)
            if not source_node:
                print('no source {} {}'.format(source_node, target_node))
            if not target_node:
                print('no target {} {}'.format(source_node, target_node))
            if not check_if_edge_exist(source_node.attrib.get('id'), target_node.attrib.get('id')):
                print('Failed to get node id {} siid {}'.format(source_node.attrib.get('id'), siid))
    # for attr in o.attrib:
    #     if


print("Finish!")
