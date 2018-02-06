import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import {IntlProvider} from 'react-intl';

import BedBlock from 'components/admissionLocation/rightPanel/bedLayout/bedBlock';
import admissionLocationFunctions from 'components/__mocks__/admissionLocationFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        admissionLocationFunctions: admissionLocationFunctions,
        loadAdmissionLocationLayout: (locationUuid) => jest.fn(),
        bed: {
            bedId: 9,
            bedNumber: '306-a',
            bedType: {
                description: 'deluxe',
                displayName: 'Double',
                id: 3
            },
            name: 'double',
            bedUuid: 'bb0a43db-d225-11e4-9c67-080027b662ec',
            columnNumber: 1,
            rowNumber: 2,
            status: 'AVAILABLE'
        },
        nullBed: {
            bedId: null,
            bedNumber: null,
            bedType: null,
            name: null,
            bedUuid: null,
            columnNumber: 2,
            rowNumber: 2,
            status: null
        }
    },
    context: {
        intl: intl
    }
};

describe('BedBlock', () => {
    it('Should render bed block properly', () => {
        const bedBlock = shallow(
            <BedBlock
                bed={testData.props.bed}
                admissionLocationFunctions={testData.props.admissionLocationFunctions}
                layoutRow={3}
                layoutColumn={3}
                loadAdmissionLocationLayout={testData.props.loadAdmissionLocationLayout}
            />,
            {context: testData.context}
        );

        expect(
            bedBlock
                .find('span')
                .text()
                .trim()
        ).toBe('306-a');
        expect(shallowToJson(bedBlock)).toMatchSnapshot();

        const noBedBlock = shallow(
            <BedBlock
                bed={testData.props.nullBed}
                admissionLocationFunctions={testData.props.admissionLocationFunctions}
                layoutRow={3}
                layoutColumn={3}
                loadAdmissionLocationLayout={testData.props.loadAdmissionLocationLayout}
            />,
            {context: testData.context}
        );

        expect(
            noBedBlock
                .find('span')
                .text()
                .trim()
        ).toBe('Add Bed');
        expect(shallowToJson(noBedBlock)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const sypOnAddEditBedHandler = jest.spyOn(BedBlock.prototype, 'addEditBedHandler');
        const sypOnDeleteHandler = jest.spyOn(BedBlock.prototype, 'onDeleteHandler');
        const spyOnSetState = jest.spyOn(testData.props.admissionLocationFunctions, 'setState');
        const bedBlock = mount(
            <BedBlock
                bed={testData.props.bed}
                admissionLocationFunctions={testData.props.admissionLocationFunctions}
                layoutRow={3}
                layoutColumn={3}
                loadAdmissionLocationLayout={testData.props.loadAdmissionLocationLayout}
            />,
            {context: testData.context}
        );

        bedBlock.find('.fa-trash').simulate('click');
        expect(sypOnDeleteHandler).toHaveBeenCalled();

        bedBlock.find('.fa-pencil').simulate('click');
        expect(sypOnAddEditBedHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEditBed',
            pageData: {
                layoutRow: 3,
                layoutColumn: 3,
                bed: testData.props.bed,
                operation: 'edit'
            }
        });
    });
});
