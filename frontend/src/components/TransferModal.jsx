import { useState } from 'react';
import { FiX, FiSend, FiArrowRight } from 'react-icons/fi';

function TransferModal({ accounts, currentAccount, onClose, onSubmit }) {
  const [targetAccount, setTargetAccount] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');

  const otherAccounts = accounts.filter(acc => acc.id !== currentAccount?.id);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(targetAccount, parseFloat(amount), description);
  };

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="card w-full max-w-md animate-slide-up">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-blue-50 flex items-center justify-center">
              <FiSend className="text-xl text-bank-accent" />
            </div>
            <h2 className="font-display text-xl font-semibold text-gray-900">Para Transfer Et</h2>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg transition-colors text-gray-500">
            <FiX />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-5 space-y-4">
          <div>
            <label className="block text-sm text-gray-500 mb-2">Gönderen Hesap</label>
            <div className="bg-gray-50 border border-gray-200 rounded-lg px-4 py-3">
              <p className="font-medium text-gray-900">{currentAccount?.accountNumber}</p>
              <p className="text-sm text-gray-500">{currentAccount?.accountHolderName}</p>
              <p className="text-sm text-bank-success mt-1">
                Bakiye: ₺{parseFloat(currentAccount?.balance || 0).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
              </p>
            </div>
          </div>

          <div className="flex justify-center">
            <FiArrowRight className="text-2xl text-gray-400" />
          </div>

          <div>
            <label className="block text-sm text-gray-500 mb-2">Alıcı Hesap</label>
            {otherAccounts.length > 0 ? (
              <select
                value={targetAccount}
                onChange={(e) => setTargetAccount(e.target.value)}
                required
                className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              >
                <option value="">Hesap seçin...</option>
                {otherAccounts.map((acc) => (
                  <option key={acc.id} value={acc.accountNumber}>
                    {acc.accountNumber} - {acc.accountHolderName}
                  </option>
                ))}
              </select>
            ) : (
              <input
                type="text"
                value={targetAccount}
                onChange={(e) => setTargetAccount(e.target.value)}
                required
                className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
                placeholder="Hesap numarası girin..."
              />
            )}
          </div>

          <div>
            <label className="block text-sm text-gray-500 mb-2">Tutar (₺)</label>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
              min="0.01"
              step="0.01"
              className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 text-2xl font-bold focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              placeholder="0.00"
            />
          </div>

          <div>
            <label className="block text-sm text-gray-500 mb-2">Açıklama (Opsiyonel)</label>
            <input
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              placeholder="Transfer açıklaması..."
            />
          </div>

          <div className="grid grid-cols-4 gap-2">
            {[100, 250, 500, 1000].map((val) => (
              <button
                key={val}
                type="button"
                onClick={() => setAmount(val.toString())}
                className="py-2 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors text-sm text-gray-700"
              >
                ₺{val}
              </button>
            ))}
          </div>

          <button
            type="submit"
            className="w-full bg-bank-accent hover:bg-blue-600 text-white py-3 rounded-lg font-medium transition-colors"
          >
            Transfer Et
          </button>
        </form>
      </div>
    </div>
  );
}

export default TransferModal;
