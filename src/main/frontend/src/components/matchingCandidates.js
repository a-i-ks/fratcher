import React from "react";

import axios from "axios";
import Avatar from 'react-avatar';
import {Button, Glyphicon, Modal} from "react-bootstrap";

import InterestsTagCloud from "./interestsTagCloud"

/**
 * I used the following tutorial as a guide, how to how and where to fetch data:
 * https://daveceddia.com/ajax-requests-in-react/
 */

class MatchingCandidates extends React.Component {

    constructor(props) {
        super();
        this.state = {
            matchingCandidates: [],
            loading: true,
            showMatchConfirmationModal: false,
            error: null,
            lastMatchedUser: null,
            lastConfirmedMatchId: null
        };

        this.likeDislikeUser = this.likeDislikeUser.bind(this);
        this.openMatchConfirmationModal = this.openMatchConfirmationModal.bind(this);
        this.closeMatchConfirmationModal = this.closeMatchConfirmationModal.bind(this);
        this.closeMatchConfirmationAndChat = this.closeMatchConfirmationAndChat.bind(this);
    }


    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/user/candidates')
            .then(({data}) => {
                console.log("received data ...");
                console.log(data);
                if (data == null) {
                    throw new Error("No matching candidates found");
                } else if (data.length == 0) {
                    throw new Error("No matching candidates found");
                } else if (data == "") {
                    throw new Error("No matching candidates found");
                }
                this.setState({
                    matchingCandidates: data,
                    loading: false,
                    error: null
                });
                this.forceUpdate();
            })
            .catch(({err}) => {
                console.error("error during load");
                console.error(err);
                this.setState({
                    loading: false,
                    error: err.message
                });
            });
    }

    renderLoading() {
        return <div style={{marginLeft: '120px'}}>Loading...</div>;
    }

    renderError() {
        return (
            <div style={{marginLeft: '120px'}}>
                An error has occurred: {this.state.error.message}
            </div>
        );
    }

    openMatchConfirmationModal() {
        this.setState({showMatchConfirmationModal: true});
    }

    closeMatchConfirmationModal() {
        this.setState({
            lastMatchedUser: null,
            showMatchConfirmationModal: false
        });
    }

    closeMatchConfirmationAndChat() {
        this.props.history.push(`/matches/${this.state.lastConfirmedMatchId}`);
        this.closeMatchConfirmationModal();
    }



    likeDislikeUser(userObj, like) {
        let apiPath = like ? "/api/match/like" : "/api/match/dislike";
        axios.post(apiPath,
            {
                id: userObj.id
            })
            .then((response) => {

                if (this.state.matchingCandidates.length > 1) {
                    this.setState({
                        matchingCandidates: this.state.matchingCandidates.slice(0, -1)
                    });
                } else {
                    this.setState({
                        matchingCandidates: null,
                    });
                }


                if (response.data.confirmed) {
                    // show "It's a Match ..." modal
                    const matchUrl = response.headers.location;
                    const matchId = matchUrl.match(/\d*[\/]*$/)[0];
                    this.setState({
                        lastConfirmedMatchId: matchId,
                        lastMatchedUser: userObj,
                        showMatchConfirmationModal: true
                    });
                }
            })
            .catch(({error}) => {
                {/*TODO Better error handling*/
                }
                console.log("error during send");
                console.log(error);
                this.setState({
                    error: true

                });
            });

    }

    renderCandidates() {

        const candidateDivStyle = {
            width: "50%",
            backgroundColor: 'white',
            border: '1px solid black',
            textAlign: 'center'
        };

        const aboutMeDivStyle = {
            margin: '10px'
        };

        const likeDislikeBtnDivStyle = {
            margin: '10px'
        };

        const likeBtnDivStyle = {
            float: 'left',
            left: '120px'
        };
        const dislikeBtnDivStyle = {};

        const Candidate = ({user}) => {
            return (
                <div style={candidateDivStyle} className="center-block">
                    <div style={{width: '17%', margin: '10px auto'}}>
                        {user.profile.imgPath &&
                        <Avatar name={user.profile.name} size={120} round={true} src={user.profile.imgPath}/>}
                        {!user.profile.imgPath &&
                        <Avatar name={user.profile.name} size={90} round={true}/>}
                    </div>
                    <h1 style={{margin: 'auto', verticalAlign: 'middle'}}>{user.profile.name}</h1>
                    <div style={aboutMeDivStyle}>{user.profile.aboutMe}</div>
                    <div>
                        <InterestsTagCloud data={user.profile.interests}/>
                    </div>
                    <div style={likeDislikeBtnDivStyle}>
                        <div style={likeBtnDivStyle}>
                            <Button onClick={() => this.likeDislikeUser(user, true)} bsStyle="success"><Glyphicon
                                glyph="thumbs-up"/> Like</Button>
                        </div>
                        <div style={dislikeBtnDivStyle}>
                            <Button onClick={() => this.likeDislikeUser(user, false)} bsStyle="danger"><Glyphicon
                                glyph="thumbs-down"/> Dislike</Button>
                        </div>
                    </div>
                </div>);
        };

        // Destructuring error and candidates from state.
        // => I don't have to write "this.state...." every time.
        const {error, matchingCandidates} = this.state;

        if (error) {
            return this.renderError();
        }

        if (matchingCandidates == null || matchingCandidates.isEmpty) {
            return (<div style={{marginLeft: "180px"}}>No candidates found! Reload page to load more.</div>)
        }

        const nextCandidate = matchingCandidates[matchingCandidates.length - 1];

        return (
            <div>
                <Candidate user={nextCandidate}/>
            </div>
        );
    }

    render() {
        const {loading} = this.state;

        let matchConfirmationModal =
            <Modal show={this.state.showMatchConfirmationModal} onHide={this.closeMatchConfirmationModal}>
                <Modal.Header closeButton>
                    <Modal.Title>It's a Match ...</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h4>You and&nbsp;
                        {this.state.lastMatchedUser != null ?
                            this.state.lastMatchedUser.profile.name : ""} are both
                        interested in getting to know each other.</h4>
                    <p></p>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.closeMatchConfirmationModal} bsStyle='primary'>Go on searching ...</Button>
                    <Button onClick={this.closeMatchConfirmationAndChat} bsStyle='success'><Glyphicon glyph="send"/>
                        Send a message</Button>
                </Modal.Footer>
            </Modal>;

        return (
            <div className="row">
                {matchConfirmationModal}
                {loading ? this.renderLoading() : this.renderCandidates()}
            </div>
        );
    }
}

export default MatchingCandidates;

