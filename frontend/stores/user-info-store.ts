import { UserInfo } from '@/types';
import { create } from 'zustand';

interface UserInfoStoreProp {
    userInfo: UserInfo | null;
    setUserInfo: (userInfo: UserInfo | null) => void;
};

export const useUserInfoStore = create<UserInfoStoreProp>((set) => ({
    userInfo: null,
    setUserInfo: (userInfo: UserInfo | null) => set({ userInfo }),
}));