import React from 'react';
import PropTypes from 'prop-types';

import BedTypeListRow from 'components/bedType/bedTypeList/bedTypelistRow';

require('./bedTypeList.css');
export default class BedTypeList extends React.Component {
    constructor(props){
        super(props);

        this.addNewHandler = this.addNewHandler.bind(this);
    }

    addNewHandler(event){
        event.preventDefault();
        this.props.bedTypeFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTypeId: null
            }
        });
    }

    render() {
        return <div className="bed-type-listing">
            <div className="block-title">
                Existing Bed Types
            </div>
            <table>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Display Name</th>
                        <th className="description">Description</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {this.props.bedTypes.map((bedType, key) => <BedTypeListRow key={key} bedType={bedType}
                        bedTypeFunctions={this.props.bedTypeFunctions}/>)}
                </tbody>
            </table>
            <button onClick={this.addNewHandler} value="Add New" className="btn btn-primary"> Add New </button>
        </div>;
    }

}

BedTypeList.propTypes = {
    bedTypes: PropTypes.array.isRequired,
    bedTypeFunctions: PropTypes.object.isRequired
};