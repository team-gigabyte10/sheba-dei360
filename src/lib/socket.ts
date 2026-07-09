type SocketEventHandler = (data: unknown) => void;

class MockSocket {
  private handlers: Record<string, SocketEventHandler[]> = {};
  connected = false;

  connect() {
    this.connected = true;
    setTimeout(() => this.emit('connect'), 100);
    return this;
  }

  disconnect() {
    this.connected = false;
    this.emit('disconnect');
  }

  on(event: string, handler: SocketEventHandler) {
    if (!this.handlers[event]) this.handlers[event] = [];
    this.handlers[event].push(handler);
    return this;
  }

  off(event: string, handler?: SocketEventHandler) {
    if (!handler) {
      delete this.handlers[event];
    } else {
      this.handlers[event] = this.handlers[event]?.filter((h) => h !== handler) ?? [];
    }
    return this;
  }

  emit(event: string, data?: unknown) {
    if (event === 'connect' || event === 'disconnect') {
      this.handlers[event]?.forEach((h) => h(null));
    } else {
      this.handlers[event]?.forEach((h) => h(data));
    }
  }

  simulateIncoming(event: string, data: unknown) {
    setTimeout(() => this.emit(event, data), 0);
  }
}

export const socket = new MockSocket();
