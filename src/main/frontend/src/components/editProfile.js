/**
 * CSS template from https://www.bootply.com/sY7gQy6XF7
 */

import React from "react";

import User from "../util/User";

import UserAvatar from "react-user-avatar";
import {Button, FormControl, FormGroup} from "react-bootstrap";

class EditProfile extends React.Component {
    constructor(props) {
        super(props);
        // this.state = {
        //
        // };
    }

    render() {
        return (
            <div className="container">
                <h1>Edit Profile</h1>
                <hr/>
                <div className="row">
                    <div className="col-md-3">
                        <div className="text-center">
                            <div className="avatar img-circle">
                                <UserAvatar size="100" name={User.profile.name}/>
                            </div>

                            <h6>Upload a different photo...</h6>

                            <input className="form-control" type="file"/>
                        </div>
                    </div>

                    <div className="col-md-9 personal-info">
                        <div className="alert alert-info alert-dismissable">
                            <a className="panel-close close" data-dismiss="alert">×</a>
                            <i className="fa fa-coffee"/>
                            This is an <strong>.alert</strong>. Use this to show important messages to the user.
                        </div>
                        <h3>Personal info</h3>
                        <form className="form-horizontal" role="form">
                            <FormGroup controlId="editProfileFirstName">
                                <label className="col-lg-3 control-label">First name:</label>
                                <div className="col-lg-8">
                                    <FormControl value="Jane" type="text"/>
                                </div>
                            </FormGroup>
                            <FormGroup controlId="editProfileLastName">
                                <label className="col-lg-3 control-label">Last name:</label>
                                <div className="col-lg-8">
                                    <FormControl value="Bishop" type="text"/>
                                </div>
                            </FormGroup>
                            <FormGroup controlId="editProfileCompany">
                                <label className="col-lg-3 control-label">Company:</label>
                                <div className="col-lg-8">
                                    <FormControl value="" type="text"/>
                                </div>
                            </FormGroup>
                            <FormGroup controlId="editProfileEmail">
                                <label className="col-lg-3 control-label">Email:</label>
                                <div className="col-lg-8">
                                    <FormControl value="janesemail@gmail.com" type="text"/>
                                </div>
                            </FormGroup>
                            <FormGroup controlId="editProfileUsername">
                                <label className="col-md-3 control-label">Username:</label>
                                <div className="col-md-8">
                                    <FormControl value="janeuser" type="text"/>
                                </div>
                            </FormGroup>
                            <FormGroup controlId="editProfilePassword">
                                <label className="col-md-3 control-label">Password:</label>
                                <div className="col-md-8">
                                    <FormControl value="11111122333" type="password"/>
                                </div>
                            </FormGroup>
                            <FormGroup controlId="editProfileConfirmPassword">
                                <label className="col-md-3 control-label">Confirm password:</label>
                                <div className="col-md-8">
                                    <FormControl value="11111122333" type="password"/>
                                </div>
                            </FormGroup>
                            <FormGroup controlId="editProfileSaveOrCancel">
                                <label className="col-md-3 control-label"/>
                                <div className="col-md-8">
                                    <Button bsClass="btn btn-primary">Save Changes</Button>
                                    <span>&nbsp;&nbsp;</span>
                                    <Button bsClass="btn btn-default" type="reset">Cancel</Button>
                                </div>
                            </FormGroup>
                        </form>
                    </div>
                </div>
            </div>
        )
    }
}

export default EditProfile;
