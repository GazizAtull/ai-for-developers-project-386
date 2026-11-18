import { useState } from 'react';
import GuestPage from './GuestPage';
import OwnerPage from './OwnerPage';

type Tab = 'guest' | 'owner';

export default function App() {
  const [tab, setTab] = useState<Tab>('guest');
  return (
    <div className="app">
      <header className="header">
        <h1>Запись на звонок</h1>
        <nav className="tabs">
          <button className={tab === 'guest' ? 'tab active' : 'tab'} onClick={() => setTab('guest')}>
            Гость
          </button>
          <button className={tab === 'owner' ? 'tab active' : 'tab'} onClick={() => setTab('owner')}>
            Владелец
          </button>
        </nav>
      </header>
      <main>{tab === 'guest' ? <GuestPage /> : <OwnerPage />}</main>
    </div>
  );
}
