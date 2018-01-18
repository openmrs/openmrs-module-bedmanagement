import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';

import BedBlock from 'components/admissionLocation/rightPanel/bedLayout/bedBlock';
import admissionLocationFunctions from 'components/__mocks__/admissionLocationFunctions-mock';

const testProps = {
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
};

describe('BedBlock', () => {
    it('Should render bed block properly', () => {
        const bedBlock = shallow(
            <BedBlock
                bed={testProps.bed}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
                layoutRow={3}
                layoutColumn={3}
                loadAdmissionLocationLayout={testProps.loadAdmissionLocationLayout}
            />
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
                bed={testProps.nullBed}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
                layoutRow={3}
                layoutColumn={3}
                loadAdmissionLocationLayout={testProps.loadAdmissionLocationLayout}
            />
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
        const spyOnSetState = jest.spyOn(testProps.admissionLocationFunctions, 'setState');
        const bedBlock = mount(
            <BedBlock
                bed={testProps.bed}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
                layoutRow={3}
                layoutColumn={3}
                loadAdmissionLocationLayout={testProps.loadAdmissionLocationLayout}
            />
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
                bed: testProps.bed,
                operation: 'edit'
            }
        });
    });
});
