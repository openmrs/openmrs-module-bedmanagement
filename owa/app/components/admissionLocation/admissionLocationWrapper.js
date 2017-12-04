import React from 'react';
import PropTypes from 'prop-types';
import Header from '../header';

class AdmissionLocationWrapper extends React.Component {
    constructor(props, context){
        super(props, context);
    }

    style = {
        container : {
            height: '100%'
        },
        wrapper : {
            marginTop: 30,
            height: '100%'
        }
    };

    render(){
        return <div style={this.style.container}>
            <Header path={this.props.match.path}/>
            <div style={this.style.wrapper}>
                Admission Location page.
            </div>
        </div>;
    }
}

AdmissionLocationWrapper.contextTypes = {
    store: PropTypes.object
};

export default AdmissionLocationWrapper;