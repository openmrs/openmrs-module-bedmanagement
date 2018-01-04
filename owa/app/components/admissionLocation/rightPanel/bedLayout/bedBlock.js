import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

export default class BedBlock extends React.PureComponent {
    constructor(props) {
        super(props);

        this.urlHelper = new UrlHelper();
        this.getBlock = this.getBlock.bind(this);
        this.addEditBedHandler = this.addEditBedHandler.bind(this);
        this.onDeleteHandler = this.onDeleteHandler.bind(this);
    }

    addEditBedHandler() {
        this.props.admissionLocationFunctions.setState({
            activePage: 'addEditBed',
            pageData: {
                layoutRow: this.props.layoutRow,
                layoutColumn: this.props.layoutColumn,
                bed: this.props.bed,
                operation: this.props.bed.bedUuid != null ? 'edit' : 'add'
            }
        });
    }

    onDeleteHandler(event) {
        event.preventDefault();
        event.stopPropagation();
        const self = this;
        const confirmation = confirm('Are you sure you want to delete bed number ' + this.props.bedNumber + '?');
        if (confirmation) {
            axios({
                method: 'delete',
                url: this.urlHelper.apiBaseUrl() + '/bed/' + this.props.bed.bedUuid,
            }).then(function () {
                self.props.admissionLocationFunctions.notify('success', 'Delete successfully');
                self.props.loadAdmissionLocationLayout(self.props.admissionLocationFunctions.getActiveLocationUuid());
            }).catch(function (error) {
                self.props.admissionLocationFunctions.notify('error', error.message);
            });
        }
    }

    getBlock() {
        if (this.props.bed.bedUuid == null) {
            return <div className="block add-block" onClick={this.addEditBedHandler}>
                <div className="left-block">
                    <i className="fa fa-plus-circle" aria-hidden="true">&nbsp;</i>
                    <span>Add Bed</span>
                </div>
            </div>;
        } else {
            return <div className="block">
                <div className="left-block">
                    <i className="fa fa-bed" aria-hidden="true">&nbsp;</i>
                    <span>{this.props.bed.bedNumber}</span>
                </div>
                <ul className="right-block">
                    <li>
                        <a href="javascript:void(0);" title="edit" onClick={this.addEditBedHandler}>
                            <i className="fa fa-pencil" aria-hidden="true"></i>
                        </a>
                    </li>
                    <li>
                        <a href="javascript:void(0);" title="delete" onClick={this.onDeleteHandler}>
                            <i className="fa fa-trash" aria-hidden="true"></i>
                        </a>
                    </li>
                </ul>
            </div>;
        }
    }

    render() {
        return this.getBlock();
    }
}

BedBlock.propTypes = {
    layoutRow: PropTypes.number.isRequired,
    layoutColumn: PropTypes.number.isRequired,
    bed: PropTypes.object.isRequired,
    loadAdmissionLocationLayout: PropTypes.func.isRequired,
    admissionLocationFunctions: PropTypes.object.isRequired
};