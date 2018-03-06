import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import {IntlProvider} from 'react-intl';
import PropTypes from 'prop-types';

import BedTypeListRow from 'components/bedType/bedTypeList/bedTypelistRow';
import bedTypeFunctionsMock from 'components/__mocks__/bedTypeFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        bedTypeFunctions: bedTypeFunctionsMock
    },
    context: {
        intl: intl
    }
};

describe('BedTypeListRow', () => {
    it('Should render bed Type list properly', () => {
        const bedTypeListRow = shallow(
            <BedTypeListRow
                bedType={testData.props.bedTypeFunctions.getBedTypeByUuid('6f9fb240-0fd5-11e8-adb7-080027b38971')}
                bedTypeFunctions={testData.props.bedTypeFunctions}
            />,
            {context: testData.context}
        );

        expect(
            bedTypeListRow
                .find('td')
                .at(0)
                .text()
                .trim()
        ).toBe('luxury');
        expect(shallowToJson(bedTypeListRow)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnDeleteHandler = jest.spyOn(BedTypeListRow.prototype, 'deleteHandler');
        const spyOnEditHandler = jest.spyOn(BedTypeListRow.prototype, 'editHandler');
        const spyOnSetState = jest.spyOn(testData.props.bedTypeFunctions, 'setState');
        const WrapperBedTypeListRow = () => {
            return (
                <table>
                    <tbody>
                        <BedTypeListRow
                            bedType={testData.props.bedTypeFunctions.getBedTypeByUuid(
                                '6f9fb240-0fd5-11e8-adb7-080027b38971'
                            )}
                            bedTypeFunctions={testData.props.bedTypeFunctions}
                        />
                    </tbody>
                </table>
            );
        };

        WrapperBedTypeListRow.contextTypes = {
            intl: PropTypes.object
        };

        const bedTypeList = mount(
            <WrapperBedTypeListRow
                bedType={testData.props.bedTypeFunctions.getBedTypeByUuid('6f9fb240-0fd5-11e8-adb7-080027b38971')}
                bedTypeFunctions={testData.props.bedTypeFunctions}
            />,
            {context: testData.context}
        );

        bedTypeList.find('.fa-edit').simulate('click');
        expect(spyOnEditHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEdit',
            pageData: {
                operation: 'edit',
                bedTypeUuid: '6f9fb240-0fd5-11e8-adb7-080027b38971'
            }
        });

        bedTypeList.find('.fa-trash').simulate('click');
        expect(spyOnDeleteHandler).toHaveBeenCalled();
    });
});
