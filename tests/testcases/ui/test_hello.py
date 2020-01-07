from automation.core.components.ui import actions


class TestHello(object):

    def test_get_ui(self, testdata):
        actions.navigate(testdata['url'])
        actions.send_keys(('id', 'kw'), testdata['text'])
        actions.click(('id', 'su'))
        actions.verify_element_text(('id', 'kw'), testdata['text'])