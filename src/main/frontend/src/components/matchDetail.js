import React from "react";

import User from "../util/User";
import InterestsTagCloud from "./interestsTagCloud"
import Chat from "./chat";


import axios from "axios";
import Avatar from 'react-avatar';

import {Button, Glyphicon, Modal} from "react-bootstrap"

class MatchDetail extends React.Component {

    constructor(props) {
        super();
        this.state = {
            matchIdFromPath: props.match.params.id,
            showDeleteConfirmationModal: false,
            match: null,
            loading: true,
            error: null
        };
        this.handleDeleteMatch = this.handleDeleteMatch.bind(this);
        this.closeDeleteConfirmationModal = this.closeDeleteConfirmationModal.bind(this);
        this.deleteMatch = this.deleteMatch.bind(this);
    }

    renderLoading() {
        return <div style={{marginLeft: '120px'}}>Loading...</div>;
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios('/api/match/' + this.state.matchIdFromPath, {
            validateStatus: (status) => {
                return (status >= 200 && status < 300) || status === 401
            }
        })
            .then(({data, status}) => {
                if (status === 401) {
                    throw new Error(data);
                }
                let user = null;
                const {user1, user2} = data;
                if (user1.id === User.id) {
                    user = user2;
                } else {
                    user = user1;
                }

                this.setState({
                    match: data,
                    user: user,
                    loading: false,
                    error: null
                });
                this.forceUpdate();
            })
            .catch((err) => {
                console.error("ERROR during load of match details: ");
                console.error(err.message);
                this.setState({
                    loading: false,
                    error: err.message
                });
                this.forceUpdate();
            });
    }

    renderError() {
        return (
            <div style={{color: 'red', marginLeft: '100px'}}>
                An error has occurred: {this.state.error}
            </div>
        );
    }

    closeDeleteConfirmationModal() {
        this.setState({
            showDeleteConfirmationModal: false
        });
    }

    handleDeleteMatch() {
        this.setState({
            showDeleteConfirmationModal: true
        });
    }

    deleteMatch() {
        this.setState({
            showDeleteConfirmationModal: true
        });
        axios('/api/match/' + this.state.matchIdFromPath, {
            method: 'delete',
            validateStatus: (status) => {
                return (status >= 200 && status < 300) || status === 401
            }
        })
            .then(({data, status}) => {
                if (status === 401) {
                    throw new Error(data);
                } else if (status === 200) {
                    this.props.history.push(`/matches/`);
                }
            })
            .catch((err) => {
                console.error("ERROR during delete of match : ");
                console.error(err.message);
                this.setState({
                    loading: false,
                    error: err.message
                });
                this.forceUpdate();
            });
    }

    renderMatch() {

        const candidateDivStyle = {
            textAlign: 'center'
        };


        // Destructuring error and matches from state.
        // => I don't have to write "this.state...." every time.
        const {error, user} = this.state;

        if (error) {
            return this.renderError();
        }

        return (
            <div style={candidateDivStyle}>
                <div>
                    <span className="pull-right">
                        <Button onClick={this.handleDeleteMatch} bsStyle='danger'><Glyphicon glyph="trash"/> Delete Match</Button>
                    </span>
                </div>
                <div style={{width: '17%', margin: '10px auto'}}>
                    {user.profile.imgPath &&
                    <Avatar name={user.profile.name} size={120} round={true} src={user.profile.imgPath}/>}
                    {!user.profile.imgPath &&
                    <Avatar name={user.profile.name} size={90} round={true}/>}
                </div>
                <h1>{user.profile.name}</h1>
                <div>{user.profile.aboutMe}</div>
                <div>
                    <InterestsTagCloud data={user.profile.interests}/>
                </div>
            </div>
        );
    }

    render() {
        const {loading, error} = this.state;
        let deleteConfirmationModal =
            <Modal show={this.state.showDeleteConfirmationModal} onHide={this.closeDeleteConfirmationModal}>
                <Modal.Header closeButton>
                    <Modal.Title>Delete Match ...</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h4>Do you really want to break up this match?</h4>
                    <p>You will then no longer be able to contact the user.</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.deleteMatch} bsStyle='danger'>Delete</Button>
                    <Button onClick={this.closeDeleteConfirmationModal}>Cancel</Button>
                </Modal.Footer>
            </Modal>;

        return (
            <div className="component">
                {deleteConfirmationModal}
                {loading ? this.renderLoading() :
                    error ? this.renderError() :
                        <div className="row">
                            <div className="col-sm-6">{this.renderMatch()}</div>
                            <div className="col-sm-6">
                                <Chat match={this.state.match}/>
                            </div>
                        </div>}
            </div>)
    }
}

export default MatchDetail;