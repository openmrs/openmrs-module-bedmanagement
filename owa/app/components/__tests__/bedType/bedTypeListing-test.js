import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import {IntlProvider} from 'react-intl';

import BedTypeList from 'components/bedType/bedTypeList';
import bedTypeFunctionsMock from 'components/__mocks__/bedTypeFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        bedTypeFunctions: bedTypeFunctionsMock,
        bedTypes: bedTypeFunctionsMock.getBedTypes()
    },
    context: {
        intl: intl
    }
};

describe('BedTypeList', () => {
    it('Should render bed Type list properly', () => {
        const bedTypeList = shallow(
            <BedTypeList bedTypes={testData.props.bedTypes} bedTypeFunctions={testData.props.bedTypeFunctions} />,
            {context: testData.context}
        );

        expect(bedTypeList.find('BedTypeListRow').length).toBe(3);
        expect(shallowToJson(bedTypeList)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnAddNewHandler = jest.spyOn(BedTypeList.prototype, 'addNewHandler');
        const spyOnSetState = jest.spyOn(testData.props.bedTypeFunctions, 'setState');
        const bedTypeList = mount(
            <BedTypeList bedTypes={testData.props.bedTypes} bedTypeFunctions={testData.props.bedTypeFunctions} />,
            {context: testData.context}
        );

        bedTypeList.find("button[value='Add New']").simulate('click');
        expect(spyOnAddNewHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTypeId: null
            }
        });
    });
});
