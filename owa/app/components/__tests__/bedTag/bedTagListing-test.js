import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import {IntlProvider} from 'react-intl';

import BedTagList from 'components/bedTag/bedTagList';
import bedTagFunctionsMock from 'components/__mocks__/bedTagFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        bedTagFunctions: bedTagFunctionsMock,
        bedTags: bedTagFunctionsMock.getBedTags()
    },
    context: {
        intl: intl
    }
};

describe('BedTagList', () => {
    it('Should render bed tag list properly', () => {
        const bedTagList = shallow(
            <BedTagList bedTags={testData.props.bedTags} bedTagFunctions={testData.props.bedTagFunctions} />,
            {context: testData.context}
        );

        expect(bedTagList.find('BedTagListRow').length).toBe(4);
        expect(shallowToJson(bedTagList)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnAddNewHandler = jest.spyOn(BedTagList.prototype, 'addNewHandler');
        const spyOnSetState = jest.spyOn(testData.props.bedTagFunctions, 'setState');
        const bedTagList = mount(
            <BedTagList bedTags={testData.props.bedTags} bedTagFunctions={testData.props.bedTagFunctions} />,
            {context: testData.context}
        );

        bedTagList.find("button[value='Add New']").simulate('click');
        expect(spyOnAddNewHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTagId: null
            }
        });
    });
});
