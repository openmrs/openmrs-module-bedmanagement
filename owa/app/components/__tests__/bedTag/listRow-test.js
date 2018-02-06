import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import {IntlProvider} from 'react-intl';
import PropTypes from 'prop-types';

import BedTagListRow from 'components/bedTag/bedTagList/bedTagListRow';
import bedTagFunctionsMock from 'components/__mocks__/bedTagFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        bedTagFunctions: bedTagFunctionsMock
    },
    context: {
        intl: intl
    }
};

describe('BedTagListRow', () => {
    it('Should render bed tag list properly', () => {
        const bedTagListRow = shallow(
            <BedTagListRow
                bedTag={testData.props.bedTagFunctions.getBedTagByUuid('ff7ed494-7b9c-4478-812a-5187e297f94c')}
                bedTagFunctions={testData.props.bedTagFunctions}
            />,
            {context: testData.context}
        );

        expect(
            bedTagListRow
                .find('td')
                .at(0)
                .text()
                .trim()
        ).toBe('Isolation');
        expect(shallowToJson(bedTagListRow)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnDeleteHandler = jest.spyOn(BedTagListRow.prototype, 'deleteHandler');
        const spyOnEditHandler = jest.spyOn(BedTagListRow.prototype, 'editHandler');
        const spyOnSetState = jest.spyOn(testData.props.bedTagFunctions, 'setState');
        const WrapperBedTagListRow = () => {
            return (
                <table>
                    <tbody>
                        <BedTagListRow
                            bedTag={testData.props.bedTagFunctions.getBedTagByUuid(
                                'ff7ed494-7b9c-4478-812a-5187e297f94c'
                            )}
                            bedTagFunctions={testData.props.bedTagFunctions}
                        />
                    </tbody>
                </table>
            );
        };

        WrapperBedTagListRow.contextTypes = {
            intl: PropTypes.object
        };

        const bedTagList = mount(
            <WrapperBedTagListRow
                bedTag={testData.props.bedTagFunctions.getBedTagByUuid('ff7ed494-7b9c-4478-812a-5187e297f94c')}
                bedTagFunctions={testData.props.bedTagFunctions}
            />,
            {context: testData.context}
        );

        bedTagList.find('.fa-edit').simulate('click');
        expect(spyOnEditHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEdit',
            pageData: {
                operation: 'edit',
                bedTagUuid: 'ff7ed494-7b9c-4478-812a-5187e297f94c'
            }
        });

        bedTagList.find('.fa-trash').simulate('click');
        expect(spyOnDeleteHandler).toHaveBeenCalled();
    });
});
