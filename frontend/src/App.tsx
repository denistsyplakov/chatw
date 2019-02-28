import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {Client} from "stompjs";

interface AppState {
    messages: string[] | null,
    username: string,
    nameIsSet: boolean,
    connectionEstablished : boolean,
}

interface AppProps {
}

class App extends Component<AppProps, AppState> {

    state: AppState;

    private client: Client;

    public componentWillMount() {
        this.setState({
            messages: new Array<string>(),
            username: "",
            nameIsSet: false,
            connectionEstablished : false,
        });
    }

    constructor(props: any) {
        super(props);

        // init stomp, create chanel
        const socket = new SockJS('/ws-main');
        this.client = Stomp.over(socket);
        this.client.connect({},
            (frame) => {
                console.log("Connected", frame);
                const url = socket["_transport"]["url"];
                const sessionId = url.split('/')[5];
                console.log("SessionId", sessionId);

                this.client.subscribe(`/topic/u-${sessionId}`, (msg) => {
                    this.onMessage(msg);
                });
                this.setState({connectionEstablished:true});
            }
        );

        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleNameSubmit = this.handleNameSubmit.bind(this);

    }

    handleNameChange(event) {
        this.setState({username: event.target.value});
    }

    handleNameSubmit(event) {
        event.preventDefault();
        console.log('Submitting username: ' + this.state.username);
        this.client.send("/app/set-name", {}, JSON.stringify({'name': this.state.username}));
    }

    onMessage(msg: any): void {
        const data = JSON.parse(msg.body);
        if (data.command == 'CommandSetName') {
            if (data.status === 'OK') {
                console.log("Name is successfully set");
                this.setState({nameIsSet: true});
            } else {
                alert(`Can not set the name ${data.error}`);
            }
        }else{
            console.log(`Reply to unknown command ${data.command}`);
        }
        console.log(`Procesed message ${msg}`);
    }

    render() {
        const {messages, username, nameIsSet,connectionEstablished} = this.state;

        const connecting = <div>Connecting to the server</div>;

        const chatLayout =
            <div id="ChatLayout">
                <div id="w-header" className="widget">
                    header
                </div>
                <div id="w-messages" className="widget">
                    messages {messages}
                </div>
                <div id="w-users" className="widget">
                    users
                </div>
                <div id="w-input" className="widget">
                    input
                </div>
            </div>;

        const setNameLayout =
            <div>
                <form onSubmit={this.handleNameSubmit}>
                    First, you need to set your name: &nbsp;
                    <input type="text" value={this.state.username} onChange={this.handleNameChange}/>
                    <button>Set</button>
                </form>
            </div>;

        if (connectionEstablished) {
            if (nameIsSet) {
                return chatLayout;
            } else {
                return setNameLayout;
            }
        }else{
            return connecting;
        }
    }
}

export default App;

