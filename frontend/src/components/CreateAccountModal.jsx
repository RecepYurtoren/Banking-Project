import { useState } from 'react';
import { FiX, FiSave, FiCreditCard } from 'react-icons/fi';

function CreateAccountModal({ onClose, onSubmit }) {
  const [accountType, setAccountType] = useState('SAVINGS');
  const [formData, setFormData] = useState({
    accountHolderName: '',
    email: '',
    initialBalance: '',
    minimumBalance: '100',
    interestRate: '2.5',
    overdraftLimit: '500',
    monthlyFee: '10'
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    const data = {
      accountType,
      accountHolderName: formData.accountHolderName,
      email: formData.email,
      initialBalance: parseFloat(formData.initialBalance) || 0,
    };

    if (accountType === 'SAVINGS') {
      data.minimumBalance = parseFloat(formData.minimumBalance) || 100;
      data.interestRate = parseFloat(formData.interestRate) || 2.5;
    } else {
      data.overdraftLimit = parseFloat(formData.overdraftLimit) || 500;
      data.monthlyFee = parseFloat(formData.monthlyFee) || 10;
    }

    onSubmit(data);
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="card w-full max-w-md animate-slide-up">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <h2 className="font-display text-xl font-semibold text-gray-900">Yeni Hesap Oluştur</h2>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg transition-colors text-gray-500">
            <FiX />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-5 space-y-4">
          <div>
            <label className="block text-sm text-gray-500 mb-2">Hesap Türü</label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setAccountType('SAVINGS')}
                className={`
                  flex items-center justify-center gap-2 p-4 rounded-xl border transition-all
                  ${accountType === 'SAVINGS' 
                    ? 'bg-amber-50 border-amber-400 text-amber-600' 
                    : 'border-gray-200 hover:border-gray-300 text-gray-500'}
                `}
              >
                <FiSave />
                <span>Birikim</span>
              </button>
              <button
                type="button"
                onClick={() => setAccountType('CHECKING')}
                className={`
                  flex items-center justify-center gap-2 p-4 rounded-xl border transition-all
                  ${accountType === 'CHECKING' 
                    ? 'bg-blue-50 border-bank-accent text-bank-accent' 
                    : 'border-gray-200 hover:border-gray-300 text-gray-500'}
                `}
              >
                <FiCreditCard />
                <span>Vadesiz</span>
              </button>
            </div>
          </div>

          <div>
            <label className="block text-sm text-gray-500 mb-2">Hesap Sahibi</label>
            <input
              type="text"
              name="accountHolderName"
              value={formData.accountHolderName}
              onChange={handleChange}
              required
              className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              placeholder="Ad Soyad"
            />
          </div>

          <div>
            <label className="block text-sm text-gray-500 mb-2">E-posta</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              placeholder="email@example.com"
            />
          </div>

          <div>
            <label className="block text-sm text-gray-500 mb-2">Başlangıç Bakiyesi (₺)</label>
            <input
              type="number"
              name="initialBalance"
              value={formData.initialBalance}
              onChange={handleChange}
              min="0"
              step="0.01"
              className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              placeholder="0.00"
            />
          </div>

          {accountType === 'SAVINGS' ? (
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm text-gray-500 mb-2">Min. Bakiye (₺)</label>
                <input
                  type="number"
                  name="minimumBalance"
                  value={formData.minimumBalance}
                  onChange={handleChange}
                  min="0"
                  step="0.01"
                  className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-500 mb-2">Faiz Oranı (%)</label>
                <input
                  type="number"
                  name="interestRate"
                  value={formData.interestRate}
                  onChange={handleChange}
                  min="0"
                  max="100"
                  step="0.1"
                  className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
                />
              </div>
            </div>
          ) : (
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm text-gray-500 mb-2">Kredi Limiti (₺)</label>
                <input
                  type="number"
                  name="overdraftLimit"
                  value={formData.overdraftLimit}
                  onChange={handleChange}
                  min="0"
                  step="0.01"
                  className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
                />
              </div>
              <div>
                <label className="block text-sm text-gray-500 mb-2">Aylık Ücret (₺)</label>
                <input
                  type="number"
                  name="monthlyFee"
                  value={formData.monthlyFee}
                  onChange={handleChange}
                  min="0"
                  step="0.01"
                  className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
                />
              </div>
            </div>
          )}

          <button
            type="submit"
            className="w-full bg-bank-accent hover:bg-blue-600 text-white py-3 rounded-lg font-medium transition-colors"
          >
            Hesap Oluştur
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreateAccountModal;
