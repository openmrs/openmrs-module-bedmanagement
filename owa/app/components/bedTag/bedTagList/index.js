import React from 'react';
import PropTypes from 'prop-types';

import BedTagListRow from 'components/bedTag/bedTagList/bedTagListRow';

require('./bedTagList.css');
export default class BedTagList extends React.Component {
    constructor(props) {
        super(props);

        this.addNewHandler = this.addNewHandler.bind(this);
    }

    addNewHandler(event) {
        event.preventDefault();
        this.props.bedTagFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTagId: null
            }
        });
    }

    render() {
        return (
            <fieldset className="bed-Tag-listing">
                <legend>&nbsp; Existing Bed Tags &nbsp;</legend>
                <table>
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th className="description">Description</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.bedTags.map((bedTag, key) => (
                            <BedTagListRow key={key} bedTag={bedTag} bedTagFunctions={this.props.bedTagFunctions} />
                        ))}
                    </tbody>
                </table>
                <button onClick={this.addNewHandler} value="Add New" className="list-btn">
                    {' '}
                    Add New{' '}
                </button>
            </fieldset>
        );
    }
}

BedTagList.propTags = {
    bedTags: PropTypes.array.isRequired,
    bedTagFunctions: PropTypes.object.isRequired
};
