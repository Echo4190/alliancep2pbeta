#define UNICODE
#define _UNICODE
#include <windows.h>
#include <commctrl.h>
#include "resource.h"

#define ID_BUTTON 1
#define IDM_ABOUT 3

static LPWSTR szInfo;
static LPWSTR szSourceFile;
static LPWSTR szTargetFile;
static LPWSTR szBackupFile;
static HWND hwndPrgBar;
static HWND hwndStatic;

LRESULT CALLBACK WndProc(HWND, UINT, WPARAM, LPARAM);
HINSTANCE g_hinst;
void CenterWindow(HWND);
void AddMenus(HWND);
DWORD CALLBACK CopyProgressRoutine(LARGE_INTEGER, LARGE_INTEGER, LARGE_INTEGER, LARGE_INTEGER, DWORD, DWORD, HANDLE, HANDLE, LPVOID);

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow) {
    int nArgs;
    LPWSTR *szArglist;

    szArglist = CommandLineToArgvW(GetCommandLineW(), &nArgs);

    if (NULL == szArglist || nArgs != 5) {
        MessageBox(NULL, TEXT("Nothing to update"), TEXT("Alliance Updater"), MB_OK);
        return 0;
    }

    szSourceFile = szArglist[1];
    szTargetFile = szArglist[2];

    szBackupFile = new WCHAR[MAX_PATH];
    szBackupFile[0] = 0;

    szInfo = new WCHAR[MAX_PATH];
    szInfo[0] = 0;
    wcsncat(szInfo, TEXT("Alliance will update to: v"), MAX_PATH);
    wcsncat(szInfo, szArglist[3], MAX_PATH);
    wcsncat(szInfo, TEXT(" build ("), MAX_PATH);
    wcsncat(szInfo, szArglist[4], MAX_PATH);
    wcsncat(szInfo, TEXT(")"), MAX_PATH);

    int extensionLength = 3;
    wcsncat(szBackupFile, szTargetFile, wcslen(szTargetFile) - extensionLength);
    wcsncat(szBackupFile, TEXT("bak"), MAX_PATH);

    HWND hwnd;
    MSG msg;
    WNDCLASS wc = {0};
    wc.lpszClassName = TEXT("Alliance Updater");
    wc.hInstance = hInstance;
    wc.hbrBackground = GetSysColorBrush(COLOR_3DFACE);
    wc.lpfnWndProc = WndProc;
    wc.hCursor = LoadCursor(0, IDC_ARROW);
    wc.hIcon = LoadIcon(hInstance, MAKEINTRESOURCE(IDI_ICON));

    g_hinst = hInstance;

    RegisterClass(&wc);
    hwnd = CreateWindow(wc.lpszClassName, TEXT("Alliance Updater"),
            WS_OVERLAPPEDWINDOW | WS_VISIBLE,
            100, 100, 260, 160, 0, 0, hInstance, 0);

    //Remove ability to resize
    HMENU hMenu = GetSystemMenu(hwnd, FALSE);
    DeleteMenu(hMenu, SC_MAXIMIZE, MF_BYCOMMAND);
    DeleteMenu(hMenu, SC_SIZE, MF_BYCOMMAND);

    while (GetMessage(&msg, NULL, 0, 0)) {
        DispatchMessage(&msg);
    }
    return (int) msg.wParam;
}

LRESULT CALLBACK WndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {

    static HWND upgradeButton;
    static bool updateDone = false;

    //Set System Font
    HFONT hfont = (HFONT) GetStockObject(DEFAULT_GUI_FONT);
    if (hfont == NULL)
        hfont = (HFONT) GetStockObject(ANSI_VAR_FONT);

    switch (msg) {
        case WM_CREATE:
            AddMenus(hwnd);
            CenterWindow(hwnd);
            hwndStatic = CreateWindow(TEXT("static"), szInfo,
                    WS_CHILD | WS_VISIBLE,
                    30, 10, 240, 20, hwnd, (HMENU) 2, NULL, NULL);

            hwndPrgBar = CreateWindowEx(0, PROGRESS_CLASS, NULL,
                    WS_CHILD | WS_VISIBLE | PBS_SMOOTH,
                    30, 35, 190, 25, hwnd, NULL, g_hinst, NULL);

            upgradeButton = CreateWindow(TEXT("button"), TEXT("Update"),
                    WS_CHILD | WS_VISIBLE,
                    85, 70, 80, 25, hwnd, (HMENU) 1, g_hinst, NULL);

            SendMessage(hwndPrgBar, PBM_SETRANGE, 0, MAKELPARAM(0, 100));
            SendMessage(hwndPrgBar, PBM_SETSTEP, 1, 0);
            SendMessage(hwndStatic, WM_SETFONT, (WPARAM) hfont, 0);
            SendMessage(upgradeButton, WM_SETFONT, (WPARAM) hfont, 0);
            break;

        case WM_COMMAND:
            switch (LOWORD(wParam)) {
                case IDM_ABOUT:
                    MessageBox(NULL, TEXT("Alliance updater v1.0a\nCreated by Bastvera"), TEXT("About"), MB_OK);
                    break;
                case ID_BUTTON:
                    if (!updateDone) {
                        SetWindowText(hwndStatic, TEXT("Preparing a backup... Please Wait..."));
                        EnableWindow(upgradeButton, FALSE);
                        if (!CopyFileEx(szTargetFile, szBackupFile, CopyProgressRoutine, NULL, FALSE, FALSE)) {
                            MessageBox(NULL, TEXT("Preparing a backup failed"), TEXT("Info"), MB_OK);
                            PostQuitMessage(0);
                        } else {
                            SetWindowText(hwndStatic, TEXT("Updating... Please Wait..."));
                            if (!CopyFileEx(szSourceFile, szTargetFile, CopyProgressRoutine, NULL, FALSE, FALSE)) {
                                MessageBox(NULL, TEXT("Update failed"), TEXT("Info"), MB_OK);
                                PostQuitMessage(0);
                            } else {
                                SetWindowText(upgradeButton, TEXT("OK"));
                                SetWindowText(hwndStatic, TEXT("Update done. Press OK."));
                                EnableWindow(upgradeButton, TRUE);
                                updateDone = true;
                            }
                        }
                    } else {
                        SetWindowText(hwndStatic, TEXT("Launching Alliance..."));
                        ShellExecute(GetDesktopWindow(), TEXT("open"), TEXT("alliance.exe"), NULL, NULL, SW_SHOWNORMAL);
                        PostQuitMessage(0);
                    }
                    break;
            }
            break;

        case WM_DESTROY:
            PostQuitMessage(0);
            break;

        case WM_KEYDOWN:
            if (wParam == VK_ESCAPE) {
                int ret = MessageBox(NULL, TEXT("Are you sure you want to quit?"), TEXT("Message"), MB_OKCANCEL);
                if (ret == IDOK) {
                    SendMessage(hwnd, WM_CLOSE, 0, 0);
                }
            }
            break;
    }
    return DefWindowProc(hwnd, msg, wParam, lParam);
}

void CenterWindow(HWND hwnd) {
    RECT rc;

    GetWindowRect(hwnd, &rc);

    SetWindowPos(hwnd, 0,
            (GetSystemMetrics(SM_CXSCREEN) - rc.right) / 2,
            (GetSystemMetrics(SM_CYSCREEN) - rc.bottom) / 2,
            0, 0, SWP_NOZORDER | SWP_NOSIZE);
}

void AddMenus(HWND hwnd) {
    HMENU hMenubar;
    HMENU hMenu;

    hMenubar = CreateMenu();
    hMenu = CreateMenu();
    AppendMenu(hMenubar, MF_POPUP, IDM_ABOUT, TEXT("&About"));
    SetMenu(hwnd, hMenubar);
}

DWORD CALLBACK CopyProgressRoutine(
        LARGE_INTEGER TotalFileSize, LARGE_INTEGER TotalBytesTransferred, LARGE_INTEGER StreamSize, LARGE_INTEGER StreamBytesTransferred,
        DWORD dwStreamNumber, DWORD dwCallbackReason,
        HANDLE hSourceFile, HANDLE hDestinationFile, LPVOID lpData) {
    DWORD currentPosition = ((TotalBytesTransferred.LowPart / 1024) * 100) / (TotalFileSize.LowPart / 1024);
    SendMessage(hwndPrgBar, PBM_SETPOS, currentPosition, 0);
    return PROGRESS_CONTINUE;
}
