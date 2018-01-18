import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';

import BedTagList from 'components/bedTag/bedTagList';
import bedTagFunctionsMock from 'components/__mocks__/bedTagFunctions-mock';

const testProps = {
    bedTagFunctions: bedTagFunctionsMock,
    bedTags: bedTagFunctionsMock.getBedTags()
};

describe('BedTagList', () => {
    it('Should render bed tag list properly', () => {
        const bedTagList = shallow(
            <BedTagList bedTags={testProps.bedTags} bedTagFunctions={testProps.bedTagFunctions} />
        );

        expect(bedTagList.find('BedTagListRow').length).toBe(4);
        expect(shallowToJson(bedTagList)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnAddNewHandler = jest.spyOn(BedTagList.prototype, 'addNewHandler');
        const spyOnSetState = jest.spyOn(testProps.bedTagFunctions, 'setState');
        const bedTagList = mount(
            <BedTagList bedTags={testProps.bedTags} bedTagFunctions={testProps.bedTagFunctions} />
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
